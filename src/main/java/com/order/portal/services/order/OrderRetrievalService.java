package com.order.portal.services.order;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.order.portal.services.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;
import com.order.portal.repositories.OrderRepository;

import static com.order.portal.utils.TimeUtils.calculateStartAndEndOfDay;

@Service
@RequiredArgsConstructor
public class OrderRetrievalService {
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
}
