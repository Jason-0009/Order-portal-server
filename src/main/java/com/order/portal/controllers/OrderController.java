package com.order.portal.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.order.portal.services.OrderService;
import com.order.portal.models.order.Order;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<Order>> getOrderPage(Pageable pageable) {
        Page<Order> orders = orderService.findAll(pageable);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody Order order) {
        orderService.placeOrder(order);

        return ResponseEntity.ok().build();
    }
}
