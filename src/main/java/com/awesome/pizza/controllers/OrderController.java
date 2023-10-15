package com.awesome.pizza.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.awesome.pizza.models.order.Order;
import com.awesome.pizza.models.order.OrderState;
import com.awesome.pizza.services.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        Order savedOrder = orderService.placeOrder(order);

        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String id, @RequestBody OrderState newStatus) {
        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);

        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }
}
