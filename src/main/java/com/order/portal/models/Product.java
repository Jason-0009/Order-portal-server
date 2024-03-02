package com.order.portal.models;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "products")
public class Product {
    @Id
    @JsonIgnore
    private String uuid;
    private Long id;
    private String imageUrl;
    private Map<String, String> name;
    private Map<String, List<String>> ingredients;
    private float price;

    public static final String SEQUENCE_NAME = "products_sequence";
}
