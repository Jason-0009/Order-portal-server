package com.order.portal.models.order;

import lombok.Data;

import java.time.Instant;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private Instant date;
    private Double totalPrice;
    private OrderState state;
    private List<OrderItem> items;
}