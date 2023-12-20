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
    public Page<User> retrieveUsers(Pageable pageable) {
        return this.userService.retrieveUsers(pageable);
    }

    @GetMapping("/profile")
    public User retrieveAuthenticatedUserProfile(Authentication authentication) throws AccessDeniedException {
        return this.userService.retrieveAuthenticatedUserProfile(authentication);
    }

    @PutMapping("/{userId}")
    public void updateUserRole(Authentication authentication, @PathVariable String userId, @RequestBody UserRole role) throws IOException {
        this.userService.updateUserRole(authentication, userId, role);
    }
}
