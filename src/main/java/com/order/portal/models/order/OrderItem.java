package com.order.portal.models.order;

import lombok.Data;

@Data
public class OrderItem {
    private String pizzaId;
    private Integer quantity;
}