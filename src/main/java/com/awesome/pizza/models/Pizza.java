package com.awesome.pizza.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "pizzas")
public class Pizza {
    @Id
    private String id;
    private String name;
    private List<String> ingredients;
    private int price;
}