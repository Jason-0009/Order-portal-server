package com.order.portal.models;

import java.util.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String imageUrl;
    private Map<String, String> name;
    private Map<String, List<String>> ingredients;
    private float price;
}
