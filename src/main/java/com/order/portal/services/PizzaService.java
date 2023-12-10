package com.order.portal.services;

import java.util.List;

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

    public Page<Pizza> retrieveAllPizzas(Pageable pageable) {
        return pizzaRepository.findAll(pageable);
    }

    public Page<Pizza> retrievePizzasByIds(List<String> ids, Pageable pageable) {
        return pizzaRepository.findByIdIn(ids, pageable);
    }
}
