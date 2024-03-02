package com.order.portal.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    @JsonIgnore
    private String uuid;
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String imageUrl;
    private String preferredLanguage;

    public static final String SEQUENCE_NAME = "users_sequence";
}
