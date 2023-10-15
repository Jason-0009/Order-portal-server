package com.awesome.pizza.services;

import java.util.List;

import com.awesome.pizza.models.order.Order;
import com.awesome.pizza.models.order.OrderState;
import com.awesome.pizza.repositories.OrderRepository;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order placeOrder(Order order) {
        order.setState(OrderState.PENDING);

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(String id, OrderState newStatus) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setState(newStatus);

                    return orderRepository.save(order);
                })
                .orElse(null);
    }
}
