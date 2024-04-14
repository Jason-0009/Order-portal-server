package com.order.portal.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.order.portal.models.user.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Page<User> findByIdNot(Pageable pageable, Long id);
    Page<User> findByNameContainingIgnoreCaseAndIdNot(Pageable pageable, String searchTerm, Long id);
}