package com.order.portal.services;

import java.io.IOException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.util.Pair;

import org.springframework.http.HttpStatus;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.web.server.ResponseStatusException;

import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.order.portal.repositories.OrderRepository;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;

import com.order.portal.websocket.OrderHandler;
import com.order.portal.websocket.StatisticsHandler;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final AuthService authService;
    private final NotificationService notificationService;

    private final OrderHandler orderHandler;
    private final StatisticsHandler statisticsHandler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public Page<Order> retrieveOrders(Pageable pageable, Instant date, OrderStatus status) {
        List<Order> orders;

        if (date == null && status == null) {
            orders = this.orderRepository.findAll();

            return sortOrders(orders, pageable);
        }

        if (date == null) {
            orders = this.orderRepository.findByStatus(status);

            return sortOrders(orders, pageable);
        }

        Pair<Instant, Instant> startAndEndOfDay = calculateStartAndEndOfDay(date);

        Instant startOfDay = startAndEndOfDay.getFirst();
        Instant endOfDay = startAndEndOfDay.getSecond();

        orders = (status == null) ? this.orderRepository.findByDateBetween(startOfDay, endOfDay)
                : this.orderRepository.findByDateBetweenAndStatus(startOfDay, endOfDay, status);

        return sortOrders(orders, pageable);
    }

    public Page<Order> retrieveOrdersForAuthenticatedUser(Authentication authentication, Pageable pageable,
                                                          Instant date, OrderStatus status) throws AccessDeniedException {
        List<Order> orders;

        OAuthAccount oauthAccount = this.authService.retrieveAuthenticatedOAuthAccount(authentication);
        String customerId = oauthAccount.getUserId();

        if (date == null && status == null) {
            orders = this.orderRepository.findByCustomerId(customerId);

            return sortOrders(orders, pageable);
        }

        if (date == null) {
            orders = this.orderRepository.findByCustomerIdAndStatus(customerId, status);

            return sortOrders(orders, pageable);
        }

        Pair<Instant, Instant> startAndEndOfDay = calculateStartAndEndOfDay(date);

        Instant startOfDay = startAndEndOfDay.getFirst();
        Instant endOfDay = startAndEndOfDay.getSecond();

        orders = status == null ? this.orderRepository.findByCustomerIdAndDateBetween(customerId, startOfDay, endOfDay)
                : this.orderRepository.findByCustomerIdAndDateBetweenAndStatus(customerId, startOfDay, endOfDay, status);

        return sortOrders(orders, pageable);
    }

    public Order retrieveOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found."));
    }

    public Map<String, Long> retrieveStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        statistics.put("deliveredToday", countOrdersDeliveredToday());
        statistics.put("pending", countPendingOrders());
        statistics.put("delivering", countDeliveringOrders());

        return statistics;
    }

    public void submitNewOrderForUser(Authentication authentication, Order order) throws AccessDeniedException {
        OAuthAccount oauthAccount = this.authService.retrieveAuthenticatedOAuthAccount(authentication);

        order.setCustomerId(oauthAccount.getUserId());
        order.setDate(Instant.now());

        this.orderRepository.save(order);
    }

    public void updateOrderStatus(Authentication authentication, String orderId, OrderStatus status) throws IOException {
        boolean orderInChargeExists = this.orderRepository.existsByStatus(OrderStatus.IN_CHARGE);

        if (status == OrderStatus.IN_CHARGE && orderInChargeExists)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un ordine è già stato preso in carico.");

        Order order = retrieveOrderById(orderId);
        order.setStatus(status);

        this.orderRepository.save(order);

        if (!(authentication instanceof OAuth2AuthenticationToken token)) return;

        String adminId = token.getPrincipal().getAttribute("sub");

        this.sendUpdatedOrder(adminId, order);
        this.sendUpdatedStatistics(adminId);

        OAuthAccount customerAccount = this.authService.retrieveOAuthAccountByUserId(order.getCustomerId());

        String statusName = status.name().toLowerCase();
        String[] parts = statusName.split("_");

        String messageCode = "orderStatus" +
                parts[0].substring(0, 1).toUpperCase() +
                parts[0].substring(1);

        if (parts.length > 1)
            messageCode += parts[1].substring(0, 1).toUpperCase() +
                    parts[1].substring(1);

        String redirectUrl = "/orders/" + orderId;

        notificationService.saveNotification(customerAccount, messageCode, redirectUrl);
    }

    private Page<Order> sortOrders(List<Order> orders, Pageable pageable) {
        List<OrderStatus> statusOrder = List.of(OrderStatus.IN_CHARGE, OrderStatus.DELIVERING,
                OrderStatus.PENDING, OrderStatus.DELIVERED);

        Comparator<Order> comparator = Comparator
                .<Order>comparingInt(order -> statusOrder.indexOf(order.getStatus()))
                .thenComparing(Order::getDate, Comparator.reverseOrder());

        List<Order> sortedOrders = orders.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedOrders.size());

        return new PageImpl<>(sortedOrders.subList(start, end), pageable, sortedOrders.size());
    }

    private Pair<Instant, Instant> calculateStartAndEndOfDay(Instant date) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime dateTimeInUserZone = date.atZone(zoneId);

        Instant startOfDay = dateTimeInUserZone
                .toLocalDate()
                .atStartOfDay(dateTimeInUserZone.getZone())
                .toInstant();

        Instant endOfDay = dateTimeInUserZone
                .toLocalDate()
                .atStartOfDay(dateTimeInUserZone.getZone())
                .plusDays(1)
                .minusNanos(1)
                .toInstant();

        return Pair.of(startOfDay, endOfDay);
    }

    private long countOrdersDeliveredToday() {
        Instant now = Instant.now();

        Pair<Instant, Instant> startAndEndOfDay = calculateStartAndEndOfDay(now);

        Instant startOfDay = startAndEndOfDay.getFirst();
        Instant endOfDay = startAndEndOfDay.getSecond();

        return this.orderRepository.countByDateBetweenAndStatus(startOfDay, endOfDay, OrderStatus.DELIVERED);
    }

    private long countPendingOrders() {
        return this.orderRepository.countByStatus(OrderStatus.PENDING);
    }

    private long countDeliveringOrders() {
        return this.orderRepository.countByStatus(OrderStatus.DELIVERING);
    }

    private void sendUpdatedOrder(String oauthUserId, Order order) throws IOException {
        String orderJson = this.objectMapper.writeValueAsString(order);

        orderHandler.sendMessage(oauthUserId, orderJson);
    }

    private void sendUpdatedStatistics(String recipientId) throws IOException {
        Map<String, Long> statistics = this.retrieveStatistics();

        String statisticsJson = this.objectMapper.writeValueAsString(statistics);

        statisticsHandler.sendMessage(recipientId, statisticsJson);
    }
}
