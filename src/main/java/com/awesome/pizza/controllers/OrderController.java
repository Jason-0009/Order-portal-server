package com.awesome.pizza.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.awesome.pizza.models.order.Order;
import com.awesome.pizza.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<Order>> getOrderPage(Pageable pageable) {
        Page<Order> orders = orderService.findAll(pageable);

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
