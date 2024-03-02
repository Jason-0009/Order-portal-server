package com.order.portal.repositories;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.order.portal.models.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Page<Product> findByIdIn(List<Long> ids, Pageable pageable);
}