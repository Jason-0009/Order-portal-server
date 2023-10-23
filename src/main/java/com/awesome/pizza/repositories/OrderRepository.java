package com.awesome.pizza.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.awesome.pizza.models.order.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

}