package com.order.portal.controllers;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.portal.models.user.User;

import com.order.portal.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public User getProfile(Authentication authentication) throws AccessDeniedException {
        return userService.getUserProfile(authentication);
    }
}
