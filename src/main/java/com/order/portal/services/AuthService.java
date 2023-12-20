package com.order.portal.services;

import java.util.NoSuchElementException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;

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

    public boolean checkAuthenticationStatus(Authentication authentication) {
        return authentication != null &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

    public OAuthAccount retrieveAuthenticatedOAuthAccount(Authentication authentication) throws AccessDeniedException {
        boolean isAuthenticated = checkAuthenticationStatus(authentication);

        if (!isAuthenticated) throw new AccessDeniedException("User is not authenticated.");

        OidcUser principal = (OidcUser) authentication.getPrincipal();

        String oauthUserId = principal.getAttribute("sub");

        return this.oauthAccountRepository.findByOauthUserId(oauthUserId)
                .orElseThrow(() -> new NoSuchElementException("OAuthAccount not found."));
    }

    public OAuthAccount retrieveOAuthAccountByUserId(String userId) throws NoSuchElementException {
        return this.oauthAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("OAuthAccount not found for userId: %s.", userId)));
    }
}
