package com.order.portal.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.time.Instant;

@Document(collection = "notifications")
@Data
public class Notification {
    @Id
    private String id;
    private String userId;
    private String message;
    private Instant date;
    private boolean readStatus;
}
