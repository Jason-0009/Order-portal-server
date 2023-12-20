package com.order.portal.services;

import java.util.NoSuchElementException;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.order.portal.repositories.UserRepository;

import com.order.portal.models.user.UserRole;
import com.order.portal.models.user.User;
import com.order.portal.models.OAuthAccount;

import com.order.portal.websocket.UserHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final AuthService authService;
    private final NotificationService notificationService;

    private final UserHandler userHandler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public Page<User> retrieveUsers(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    public User retrieveAuthenticatedUserProfile(Authentication authentication) throws AccessDeniedException {
        OAuthAccount oauthAccount = this.authService.retrieveAuthenticatedOAuthAccount(authentication);

        return this.retrieveUserById(oauthAccount.getUserId());
    }

    public void updateUserRole(Authentication authentication, String userId, UserRole role) throws IOException {
        User user = this.retrieveUserById(userId);
        user.setRole(role);

        this.userRepository.save(user);

        if (!(authentication instanceof OAuth2AuthenticationToken token)) return;

        String adminId = token.getPrincipal().getAttribute("sub");

        this.sendUpdatedUser(adminId, user);

        OAuthAccount userAccount = this.authService.retrieveOAuthAccountByUserId(userId);
        String notificationMessage = String.format("Il tuo ruolo è stato aggiornato ad %s", role);

        notificationService.saveNotification(userAccount, notificationMessage);
    }

    private User retrieveUserById(String id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
    }

    private void sendUpdatedUser(String recipientId, User user) throws IOException {
        String userJson = this.objectMapper.writeValueAsString(user);

        userHandler.sendMessage(recipientId, userJson);
    }
}
