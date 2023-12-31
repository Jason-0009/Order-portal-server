package com.order.portal.repositories;

import java.util.Optional;

import org.springframework.data.domain.*;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.order.portal.models.user.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Page<User> findByNameContainingIgnoreCase(Pageable pageable, String searchTerm);
}