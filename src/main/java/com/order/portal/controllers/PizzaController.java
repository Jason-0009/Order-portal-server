package com.order.portal.controllers;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Page<Pizza>> retrievePizzas(@RequestParam Optional<List<String>> ids,
            Pageable pageable) {
        Page<Pizza> pizzas = ids.isPresent() ? pizzaService.retrievePizzasByIds(ids.get(), pageable)
                : pizzaService.retrieveAllPizzas(pageable);

        return new ResponseEntity<>(pizzas, HttpStatus.OK);
    }
}
