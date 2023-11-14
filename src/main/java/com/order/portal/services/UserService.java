package com.order.portal.services;

import org.springframework.stereotype.Service;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.user.User;

import com.order.portal.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthService authService;
    private final UserRepository userRepository;

    public User getUserProfile() {
        OAuthAccount oauthAccount = authService.getAuthenticatedOAuthAccount();

        return userRepository.findById(oauthAccount.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found."));
    }
}
