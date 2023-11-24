package com.order.portal.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.order.portal.services.OrderService;
import com.order.portal.models.order.Order;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/user")
    public ResponseEntity<Page<Order>> getOrdersByUser(Authentication authentication, Pageable pageable)
            throws AccessDeniedException {
        Page<Order> orders = orderService.getOrdersByUser(authentication, pageable);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderById(orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> placeOrder(Authentication authentication, @RequestBody Order order)
            throws AccessDeniedException {
        orderService.placeOrder(authentication, order);

        return ResponseEntity.ok().build();
    }
}
