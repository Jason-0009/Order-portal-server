package com.awesome.pizza.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.awesome.pizza.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}