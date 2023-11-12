package com.order.portal.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.order.portal.models.user.User;
import com.order.portal.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal;
            String providerUserId = oidcUser.getSubject();

            return userRepository.findByProviderUserId(providerUserId)
                    .orElseThrow(() -> new RuntimeException("User not found."));
        } else {
            throw new RuntimeException("Principal is not an OidcUser.");
        }
    }
}
