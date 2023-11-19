package com.order.portal.services;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.user.User;

import com.order.portal.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthService authService;
    private final UserRepository userRepository;

    public User getUserProfile(Authentication authentication) throws AccessDeniedException {
        OAuthAccount oauthAccount = authService.getAuthenticatedOAuthAccount(authentication);

        return userRepository.findById(oauthAccount.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found."));
    }
}
