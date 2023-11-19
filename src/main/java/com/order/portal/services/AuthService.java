package com.order.portal.services;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.order.portal.models.OAuthAccount;

import com.order.portal.repositories.OAuthAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final OAuthAccountRepository oauthAccountRepository;

    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null;
    }

    public OAuthAccount getAuthenticatedOAuthAccount(Authentication authentication) throws AccessDeniedException {
        if (!isAuthenticated(authentication)) throw new AccessDeniedException("User is not authenticated.");

        OidcUser principal = (OidcUser) authentication.getPrincipal();
        
        String oauthUserId = principal.getAttribute("sub");

        return oauthAccountRepository.findByOauthUserId(oauthUserId)
                .orElseThrow(() -> new NoSuchElementException("OAuthAccount not found."));
    }
}

