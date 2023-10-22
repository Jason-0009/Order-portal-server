package com.awesome.pizza.models.order;

import lombok.Data;

@Data
public class OrderItem {
    private String pizzaId;
    private Integer quantity;
}