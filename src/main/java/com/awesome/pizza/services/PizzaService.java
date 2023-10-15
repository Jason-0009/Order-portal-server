package com.awesome.pizza.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.awesome.pizza.models.Pizza;
import com.awesome.pizza.repositories.PizzaRepository;

@Service
public class PizzaService {
    private final PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }
}
