package com.order.portal.models;

import java.time.Instant;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "notifications")
@Data
public class Notification {
    @Id
    @JsonIgnore
    private String uuid;
    private Long id;
    private Long userId;
    private String messageCode;
    private Instant date;
    private boolean readStatus;
    private String redirectUrl;

    public static final String SEQUENCE_NAME = "notifications_sequence";
}
