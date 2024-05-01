package com.order.portal.services.user;

import java.util.NoSuchElementException;
import java.util.Objects;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.order.portal.repositories.UserRepository;

import com.order.portal.models.OAuthAccount;

import com.order.portal.models.user.User;
import com.order.portal.models.user.UserRole;

import com.order.portal.services.AuthService;
import com.order.portal.services.NotificationService;

import com.order.portal.websocket.UserHandler;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final AuthService authService;
    private final NotificationService notificationService;

    private final UserHandler userHandler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public Page<User> retrieveUsers(Authentication authentication, Pageable pageable, String searchTerm) {
        OAuthAccount oauthAccount = authService.retrieveAuthenticatedOAuthAccount(authentication);

        Long authenticatedUserId = oauthAccount.getUserId();

        Sort sort = Sort.by(Sort.Direction.ASC, "role");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        if (searchTerm == null || searchTerm.isEmpty())
            return userRepository.findByIdNot(sortedPageable, authenticatedUserId);

        return userRepository.findByNameContainingIgnoreCaseAndIdNot(sortedPageable, searchTerm, authenticatedUserId);
    }

    public User retrieveAuthenticatedUserProfile(Authentication authentication) throws AccessDeniedException {
        OAuthAccount oauthAccount = authService.retrieveAuthenticatedOAuthAccount(authentication);

        return Objects.requireNonNull(retrieveUserById(oauthAccount.getUserId()));
    }

    public void updateUserRole(Long userId, UserRole role) throws IOException {
        User user = retrieveUserById(userId);
        user.setRole(role);

        userRepository.save(user);

        sendUpdatedUser(user);

        OAuthAccount userAccount = authService.retrieveOAuthAccountByUserId(userId);

        String messageCode = "roleUpdatedTo" + role.name().substring(0, 1).toUpperCase() +
                role.name().substring(1).toLowerCase();

        String redirectUrl = Objects.equals(user.getRole(), UserRole.ADMIN) ? "/admin/users" : null;

        notificationService.saveNotification(userAccount, messageCode, redirectUrl);
    }

    public void updateUserPreferredLanguage(Long userId, String preferredLanguage) {
        User user = retrieveUserById(userId);
        user.setPreferredLanguage(preferredLanguage);

        userRepository.save(user);
    }

    public User retrieveUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));
    }

    private void sendUpdatedUser(User user) throws IOException {
        String userJson = objectMapper.writeValueAsString(user);

        userHandler.broadcastMessage(userJson);
    }
}
