package com.awesome.pizza.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.awesome.pizza.models.Pizza;

@Repository
public interface PizzaRepository extends PagingAndSortingRepository<Pizza, String> {

}