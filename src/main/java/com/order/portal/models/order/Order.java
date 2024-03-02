package com.order.portal.models.order;

import java.time.Instant;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    @JsonIgnore
    private String uuid;
    private Long id;
    private Long customerId;
    private Instant date;
    private Double totalPrice;
    private OrderStatus status;
    private List<OrderItem> items;

    public static final String SEQUENCE_NAME = "orders_sequence";

}