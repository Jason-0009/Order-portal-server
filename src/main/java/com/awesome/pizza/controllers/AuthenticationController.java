package com.awesome.pizza.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    @GetMapping("/is-authenticated")
    public boolean isAuthenticated() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return principal instanceof OidcUser;
    }
}
