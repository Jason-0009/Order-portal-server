package com.order.portal.controllers;

import java.util.Map;
import java.io.IOException;
import java.time.Instant;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.core.Authentication;

import com.order.portal.services.order.OrderRetrievalService;
import com.order.portal.services.order.OrderStatisticsService;
import com.order.portal.services.order.OrderUpdateService;

import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRetrievalService orderRetrievalService;
    private final OrderUpdateService orderUpdateService;
    private final OrderStatisticsService orderStatisticsService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public Page<Order> retrieveOrders(Pageable pageable,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant date,
                                      @RequestParam(required = false) OrderStatus status) {
        return orderRetrievalService.retrieveOrders(pageable, date, status);
    }

    @GetMapping("/user")
    public Page<Order> retrieveOrdersForAuthenticatedUser(Authentication authentication,
                                                          Pageable pageable,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant date,
                                                          @RequestParam(required = false) OrderStatus status) {
        return orderRetrievalService.retrieveOrdersForAuthenticatedUser(authentication, pageable, date, status);
    }

    @GetMapping("/{orderId}")
    public Order retrieveOrderById(@PathVariable Long orderId) {
        return orderRetrievalService.retrieveOrderById(orderId);
    }

    @GetMapping("/statistics")
    public Map<String, Long> retrieveOrderStatistics() {
        return orderStatisticsService.retrieveOrderStatistics();
    }

    @PostMapping
    public void submitNewOrderForUser(Authentication authentication, @RequestBody Order order) throws IOException {
        orderUpdateService.submitNewOrderForUser(authentication, order);
    }

    @PutMapping("/{orderId}")
    public void updateOrderStatus(@PathVariable Long orderId,
                                  @RequestBody OrderStatus status) throws IOException {
        orderUpdateService.updateOrderStatus(orderId, status);
    }
}
