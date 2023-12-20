package com.order.portal.controllers;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import com.order.portal.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/is-authenticated")
    public boolean checkUserAuthenticationStatus(Authentication authentication) {
        return this.authService.checkAuthenticationStatus(authentication);
    }
}
