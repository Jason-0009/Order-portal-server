package com.awesome.pizza.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.awesome.pizza.models.order.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
}