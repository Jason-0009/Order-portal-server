package com.order.portal.controllers;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

import com.order.portal.services.OrderService;

import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<Order>> retrieveOrders(Pageable pageable,
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant date,
                                                      @RequestParam(required = false) OrderStatus status) {
        Page<Order> orders = orderService.retrieveOrders(pageable, date, status);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<Order>> retrieveOrdersForAuthenticatedUser(Authentication authentication,
                                                                          Pageable pageable,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant date,
                                                                          @RequestParam(required = false) OrderStatus status) throws AccessDeniedException {
        Page<Order> orders = orderService.retrieveOrdersForAuthenticatedUser(authentication, pageable, date, status);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> retrieveOrderById(@PathVariable String orderId) {
        Order order = orderService.retrieveOrderById(orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> retrieveStatistics() {
        Map<String, Long> statistics = orderService.retrieveStatistics();

        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> submitNewOrderForUser(Authentication authentication, @RequestBody Order order)
            throws AccessDeniedException {
        orderService.submitNewOrderForUser(authentication, order);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderStatus status) {
        try {
            orderService.updateOrderStatus(orderId, status);

            return ResponseEntity.ok().build();
        } catch (ResponseStatusException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getReason());
        }
    }
}
