package com.awesome.pizza.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.awesome.pizza.models.Pizza;
import com.awesome.pizza.repositories.PizzaRepository;

@Service
@RequiredArgsConstructor
public class PizzaService {
    private final PizzaRepository pizzaRepository;

    public Page<Pizza> findAll(Pageable pageable) {
        return pizzaRepository.findAll(pageable);
    }
}
