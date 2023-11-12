package com.order.portal.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.order.portal.models.Pizza;

@Repository
public interface PizzaRepository extends MongoRepository<Pizza, String> {

}