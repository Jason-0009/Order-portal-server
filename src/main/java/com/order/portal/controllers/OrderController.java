package com.order.portal.controllers;

import java.util.Map;

import java.io.IOException;

import java.time.Instant;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.core.Authentication;

import com.order.portal.services.OrderService;

import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Page<Order> retrieveOrders(Pageable pageable,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant date,
                                      @RequestParam(required = false) OrderStatus status) {
        return this.orderService.retrieveOrders(pageable, date, status);
    }

    @GetMapping("/user")
    public Page<Order> retrieveOrdersForAuthenticatedUser(Authentication authentication,
                                                          Pageable pageable,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant date,
                                                          @RequestParam(required = false) OrderStatus status) {
        return this.orderService.retrieveOrdersForAuthenticatedUser(authentication, pageable, date, status);
    }

    @GetMapping("/{orderId}")
    public Order retrieveOrderById(@PathVariable String orderId) {
        return this.orderService.retrieveOrderById(orderId);
    }

    @GetMapping("/statistics")
    public Map<String, Long> retrieveStatistics() {
        return this.orderService.retrieveStatistics();
    }

    @PostMapping
    public void submitNewOrderForUser(Authentication authentication, @RequestBody Order order) {
        this.orderService.submitNewOrderForUser(authentication, order);
    }

    @PutMapping("/{orderId}")
    public void updateOrderStatus(Authentication authentication,
                                  @PathVariable String orderId,
                                  @RequestBody OrderStatus status) throws IOException {
        this.orderService.updateOrderStatus(authentication, orderId, status);
    }
}
