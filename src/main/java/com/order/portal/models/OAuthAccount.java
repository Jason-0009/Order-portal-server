package com.order.portal.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "oauth_accounts")
public class OAuthAccount {
    @Id
    private String id;
    private String oauthUserId;
    private String provider;
    private String userId;
}
