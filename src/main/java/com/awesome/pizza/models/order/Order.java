package com.awesome.pizza.models.order;

import lombok.Data;

import java.sql.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private Date date;
    private Double totalPrice;
    private OrderState state;
    private List<OrderItem> items;
    private String customerId;
}