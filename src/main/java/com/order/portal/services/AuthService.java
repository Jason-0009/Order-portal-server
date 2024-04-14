package com.order.portal.services;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.order.portal.models.OAuthAccount;

import com.order.portal.repositories.OAuthAccountRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final OAuthAccountRepository oauthAccountRepository;

    public ResponseEntity<?> checkAuthenticationStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok().build();
    }

    public OAuthAccount retrieveAuthenticatedOAuthAccount(Authentication authentication) throws AccessDeniedException {
        if (authentication == null) return null;

        OidcUser principal = (OidcUser) authentication.getPrincipal();

        String oauthUserId = principal.getAttribute("sub");

        return oauthAccountRepository.findByOauthUserId(oauthUserId)
                .orElseThrow(() -> new NoSuchElementException("OAuthAccount not found."));
    }

    public OAuthAccount retrieveOAuthAccountByUserId(Long userId) throws NoSuchElementException {
        return oauthAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("OAuthAccount not found for userId: %s.", userId)));
    }
}
