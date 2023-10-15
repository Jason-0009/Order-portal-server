package com.awesome.pizza.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.awesome.pizza.models.Pizza;

@Repository
public interface PizzaRepository extends MongoRepository<Pizza, String> {
    
}