package com.order.portal.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.order.portal.models.OAuthAccount;

public interface OAuthAccountRepository extends MongoRepository<OAuthAccount, String> {
    Optional<OAuthAccount> findByOauthUserId(String oauthUserId);
}
