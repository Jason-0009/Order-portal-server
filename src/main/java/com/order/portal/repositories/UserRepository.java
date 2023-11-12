package com.order.portal.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.order.portal.models.user.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByProviderUserId(String providerUserId);
}