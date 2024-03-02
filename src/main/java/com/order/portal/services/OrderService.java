package com.order.portal.services;

import java.io.IOException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.util.Pair;

import org.springframework.http.HttpStatus;

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
    private final SequenceGeneratorService sequenceGeneratorService;

    private final OrderHandler orderHandler;
    private final StatisticsHandler statisticsHandler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public Page<Order> retrieveOrders(Pageable pageable, Instant date, OrderStatus status) {
        List<Order> orders;

        if (date == null && status == null) {
            orders = orderRepository.findAll();

            return sortOrders(orders, pageable);
        }

        if (date == null) {
            orders = orderRepository.findByStatus(status);

            return sortOrders(orders, pageable);
        }

        Pair<Instant, Instant> startAndEndOfDay = calculateStartAndEndOfDay(date);

        Instant startOfDay = startAndEndOfDay.getFirst();
        Instant endOfDay = startAndEndOfDay.getSecond();

        orders = (status == null) ? orderRepository.findByDateBetween(startOfDay, endOfDay)
                : orderRepository.findByDateBetweenAndStatus(startOfDay, endOfDay, status);

        return sortOrders(orders, pageable);
    }

    public Page<Order> retrieveOrdersForAuthenticatedUser(Authentication authentication, Pageable pageable,
                                                          Instant date, OrderStatus status) throws AccessDeniedException {
        List<Order> orders;

        OAuthAccount oauthAccount = authService.retrieveAuthenticatedOAuthAccount(authentication);
        Long customerId = oauthAccount.getUserId();

        if (date == null && status == null) {
            orders = orderRepository.findByCustomerId(customerId);

            return sortOrders(orders, pageable);
        }

        if (date == null) {
            orders = orderRepository.findByCustomerIdAndStatus(customerId, status);

            return sortOrders(orders, pageable);
        }

        Pair<Instant, Instant> startAndEndOfDay = calculateStartAndEndOfDay(date);

        Instant startOfDay = startAndEndOfDay.getFirst();
        Instant endOfDay = startAndEndOfDay.getSecond();

        orders = status == null ? orderRepository.findByCustomerIdAndDateBetween(customerId, startOfDay, endOfDay)
                : orderRepository.findByCustomerIdAndDateBetweenAndStatus(customerId, startOfDay, endOfDay, status);

        return sortOrders(orders, pageable);
    }

    public Order retrieveOrderById(Long id) {
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

    public void submitNewOrderForUser(Authentication authentication, Order order) throws AccessDeniedException, IOException {
        OAuthAccount oauthAccount = authService.retrieveAuthenticatedOAuthAccount(authentication);

        order.setId(sequenceGeneratorService.generateSequence(Order.SEQUENCE_NAME));
        order.setCustomerId(oauthAccount.getUserId());
        order.setDate(Instant.now());

        orderRepository.save(order);

        sendUpdates(order);
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) throws IOException {
        boolean orderInChargeExists = orderRepository.existsByStatus(OrderStatus.IN_CHARGE);

        if (status == OrderStatus.IN_CHARGE && orderInChargeExists)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "orderAlreadyInCharge");

        Order order = retrieveOrderById(orderId);
        order.setStatus(status);

        orderRepository.save(order);

        sendUpdates(order);

        OAuthAccount customerAccount = authService.retrieveOAuthAccountByUserId(order.getCustomerId());

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

        return orderRepository.countByDateBetweenAndStatus(startOfDay, endOfDay, OrderStatus.DELIVERED);
    }

    private long countPendingOrders() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    private long countDeliveringOrders() {
        return orderRepository.countByStatus(OrderStatus.DELIVERING);
    }

    private void sendUpdates(Order order) throws IOException {
        sendUpdatedOrder(order);
        sendUpdatedStatistics();
    }

    private void sendUpdatedOrder(Order order) throws IOException {
        String orderJson = objectMapper.writeValueAsString(order);

        orderHandler.broadcastMessage(orderJson);
    }

    private void sendUpdatedStatistics() throws IOException {
        Map<String, Long> statistics = retrieveStatistics();

        String statisticsJson = objectMapper.writeValueAsString(statistics);

        statisticsHandler.broadcastMessage(statisticsJson);
    }
}
