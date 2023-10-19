package com.awesome.pizza.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.awesome.pizza.models.order.Order;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, String> {

}