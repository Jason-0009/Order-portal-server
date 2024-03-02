package com.order.portal.repositories;

import java.time.Instant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import com.order.portal.models.order.Order;
import com.order.portal.models.order.OrderStatus;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findById(Long id);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByDateBetween(Instant start, Instant end);

    List<Order> findByDateBetweenAndStatus(Instant start, Instant end, OrderStatus status);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);

    List<Order> findByCustomerIdAndDateBetween(Long customerId, Instant start, Instant end);

    List<Order> findByCustomerIdAndDateBetweenAndStatus(Long customerId, Instant start, Instant end, OrderStatus status);

    long countByStatus(OrderStatus status);
    long countByDateBetweenAndStatus(Instant start, Instant end, OrderStatus status);

    boolean existsByStatus(OrderStatus status);
}