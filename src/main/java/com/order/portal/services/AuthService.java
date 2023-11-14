package com.order.portal.services;

import org.springframework.stereotype.Service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.order.portal.models.OAuthAccount;

import com.order.portal.repositories.OAuthAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final OAuthAccountRepository oauthAccountRepository;

    public boolean isAuthenticated() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return principal instanceof OidcUser;
    }

    public OAuthAccount getAuthenticatedOAuthAccount() {
        OidcUser principal = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        String oauthUserId = principal.getAttribute("sub");

        return oauthAccountRepository.findByOauthUserId(oauthUserId)
                .orElseThrow(() -> new RuntimeException("OAuthAccount not found."));
    }
}

