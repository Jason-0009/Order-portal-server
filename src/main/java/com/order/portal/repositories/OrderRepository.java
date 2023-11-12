package com.order.portal.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.order.portal.models.order.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
}