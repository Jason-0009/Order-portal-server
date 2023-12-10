package com.order.portal.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import com.order.portal.models.OAuthAccount;
import com.order.portal.models.user.Role;
import com.order.portal.models.user.User;

import com.order.portal.repositories.OAuthAccountRepository;
import com.order.portal.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final OAuthAccountRepository oauthAccountRepository;

    @Value("${client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("name");
        String imageUrl = token.getPrincipal().getAttribute("picture");
        String oauthUserId = token.getPrincipal().getAttribute("sub");

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setImageUrl(imageUrl);

                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User();

                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setImageUrl(imageUrl);
                    newUser.setRole(Role.USER);

                    return userRepository.save(newUser);
                });

        oauthAccountRepository.findByOauthUserId(oauthUserId)
                .ifPresentOrElse(
                        existingOAuthAccount -> {
                        },
                        () -> {
                            OAuthAccount newOAuthAccount = new OAuthAccount();

                            newOAuthAccount.setOauthUserId(oauthUserId);
                            newOAuthAccount.setProvider(token.getAuthorizedClientRegistrationId());
                            newOAuthAccount.setUserId(user.getId());

                            oauthAccountRepository.save(newOAuthAccount);
                        });

        response.sendRedirect(clientUrl);
    }
}
