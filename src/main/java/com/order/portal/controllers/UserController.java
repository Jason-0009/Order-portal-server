package com.order.portal.controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;

import com.order.portal.models.user.UserRole;
import com.order.portal.models.user.User;
import com.order.portal.services.UserService;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Page<User> retrieveUsers(Authentication authentication,
                                    Pageable pageable,
                                    @RequestParam(required = false) String searchTerm) {
        return this.userService.retrieveUsers(authentication, pageable, searchTerm);
    }

    @GetMapping("/profile")
    public User retrieveAuthenticatedUserProfile(Authentication authentication) throws AccessDeniedException {
        return this.userService.retrieveAuthenticatedUserProfile(authentication);
    }

    @GetMapping("/{userId}")
    public User retrieveUser(@PathVariable String userId) throws AccessDeniedException {
        return this.userService.retrieveUserById(userId);
    }
    
    @PutMapping("/{userId}/role")
    public void updateUserRole(@PathVariable String userId,
                               @RequestBody UserRole role) throws IOException {
        this.userService.updateUserRole(userId, role);
    }

    @PutMapping("/{userId}/preferredLanguage")
    public void updateUserPreferredLanguage(@PathVariable String userId,
                                            @RequestBody String preferredLanguage) {
        this.userService.updateUserPreferredLanguage(userId, preferredLanguage);
    }
}
