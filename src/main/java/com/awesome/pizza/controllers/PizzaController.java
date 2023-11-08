package com.awesome.pizza.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.awesome.pizza.models.Pizza;
import com.awesome.pizza.services.PizzaService;

@RestController
@RequestMapping("/pizzas")
@RequiredArgsConstructor
public class PizzaController {
    private final PizzaService pizzaService;

    @GetMapping
    public ResponseEntity<Page<Pizza>> getPizzasPage(Pageable pageable) {
        Page<Pizza> pizzas = pizzaService.findAll(pageable);
        
        return new ResponseEntity<>(pizzas, HttpStatus.OK);
    }
}
