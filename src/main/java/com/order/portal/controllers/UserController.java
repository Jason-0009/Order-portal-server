package com.order.portal.controllers;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;

import com.order.portal.services.user.UserService;

import org.springframework.web.bind.annotation.*;

import com.order.portal.models.user.User;
import com.order.portal.models.user.UserRole;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<User> retrieveUsers(Authentication authentication,
                                    Pageable pageable,
                                    @RequestParam(required = false) String searchTerm) {
        return userService.retrieveUsers(authentication, pageable, searchTerm);
    }

    @GetMapping("/profile")
    public User retrieveAuthenticatedUserProfile(Authentication authentication) throws AccessDeniedException {
        return userService.retrieveAuthenticatedUserProfile(authentication);
    }

    @GetMapping("/{userId}")
    public User retrieveUser(@PathVariable Long userId) throws AccessDeniedException {
        return userService.retrieveUserById(userId);
    }
    
    @PutMapping("/{userId}/role")
    public void updateUserRole(@PathVariable Long userId,
                               @RequestBody UserRole role) throws IOException {
        userService.updateUserRole(userId, role);
    }

    @PutMapping("/{userId}/preferredLanguage")
    public void updateUserPreferredLanguage(@PathVariable Long userId,
                                            @RequestBody String preferredLanguage) {
        userService.updateUserPreferredLanguage(userId, preferredLanguage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/is-admin")
    public boolean isAdmin() {
        return true;
    }
}
