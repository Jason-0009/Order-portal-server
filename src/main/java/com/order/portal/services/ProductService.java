package com.order.portal.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import com.order.portal.models.Product;
import com.order.portal.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> retrieveAllProducts(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    public Page<Product> retrieveProductsByIds(List<Long> ids, Pageable pageable) {
        return this.productRepository.findByIdIn(ids, pageable);
    }
}
