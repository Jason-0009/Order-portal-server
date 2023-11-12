package com.order.portal.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.order.portal.services.PizzaService;
import com.order.portal.models.Pizza;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pizzas")
@RequiredArgsConstructor
public class PizzaController {
    private final PizzaService pizzaService;

    @GetMapping
    public ResponseEntity<Page<Pizza>> getPizzasPage(Pageable pageable) {
        Page<Pizza> pizzas = pizzaService.findAll(pageable);
        
        return new ResponseEntity<>(pizzas, HttpStatus.OK);
    }
}
