package com.order.portal.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import com.order.portal.models.Pizza;
import com.order.portal.repositories.PizzaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PizzaService {
    private final PizzaRepository pizzaRepository;

    public Page<Pizza> findAll(Pageable pageable) {
        return pizzaRepository.findAll(pageable);
    }
}
