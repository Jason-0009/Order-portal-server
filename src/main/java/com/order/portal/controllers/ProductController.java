package com.order.portal.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import com.order.portal.services.ProductService;

import com.order.portal.models.Product;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Page<Product> retrieveProducts(Pageable pageable) {
        return this.productService.retrieveAllProducts(pageable);
    }

    @GetMapping(params = "ids")
    public Page<Product> retrieveProducts(@RequestParam List<Long> ids, Pageable pageable) {
        return this.productService.retrieveProductsByIds(ids, pageable);
    }
}
