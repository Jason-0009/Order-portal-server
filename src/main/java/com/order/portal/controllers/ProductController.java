package com.order.portal.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.order.portal.services.ProductService;

import com.order.portal.models.Product;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Page<Product> retrieveProducts(Pageable pageable) {
        return productService.retrieveAllProducts(pageable);
    }

    @GetMapping(params = "ids")
    public Page<Product> retrieveProducts(@RequestParam List<Long> ids, Pageable pageable) {
        return productService.retrieveProductsByIds(ids, pageable);
    }
}
