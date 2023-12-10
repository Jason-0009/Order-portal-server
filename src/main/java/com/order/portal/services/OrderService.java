package com.order.portal.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;

import com.order.portal.repositories.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authService;

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

        String customerId = oauthAccount.getUserId();

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

    private long countOrdersDeliveredToday() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        Instant startOfDay = now
                .toLocalDate()
                .atStartOfDay(now.getZone())
                .toInstant();

        Instant endOfDay = now
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay(now.getZone())
                .toInstant();

        return this.orderRepository.countByDateBetweenAndStatus(startOfDay, endOfDay, OrderStatus.DELIVERED);
    }

    private long countPendingOrders() {
        return this.orderRepository.countByStatus(OrderStatus.PENDING);
    }

    private long countDeliveringOrders() {
        return this.orderRepository.countByStatus(OrderStatus.DELIVERING);
    }

    public void submitNewOrderForUser(Authentication authentication, Order order) throws AccessDeniedException {
        OAuthAccount oauthAccount = authService.retrieveAuthenticatedOAuthAccount(authentication);

        order.setCustomerId(oauthAccount.getUserId());
        order.setDate(Instant.now());

        orderRepository.save(order);
    }

    public void updateOrderStatus(String id, OrderStatus status) {
        if (status == OrderStatus.IN_CHARGE && orderRepository.existsByStatus(OrderStatus.IN_CHARGE))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un ordine è già stato preso in carico.");

        Order order = retrieveOrderById(id);

        order.setStatus(status);

        orderRepository.save(order);
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
        ZonedDateTime dateTimeInUserZone = date.atZone(ZoneId.systemDefault());

        Instant startOfDay = dateTimeInUserZone
                .toLocalDate()
                .atStartOfDay(dateTimeInUserZone.getZone())
                .toInstant();

        Instant endOfDay = dateTimeInUserZone
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay(dateTimeInUserZone.getZone())
                .toInstant();

        return Pair.of(startOfDay, endOfDay);
    }
}
