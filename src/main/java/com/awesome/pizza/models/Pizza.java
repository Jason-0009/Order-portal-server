package com.awesome.pizza.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "pizzas")
public class Pizza {
    @Id
    private String id;
    private String type;
    private Double price;
}