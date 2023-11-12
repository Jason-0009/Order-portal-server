package com.order.portal.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.order.portal.models.user.Role;
import com.order.portal.models.user.User;
import com.order.portal.repositories.UserRepository;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;

    @Value("${client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String providerUserId = token.getPrincipal().getAttribute("sub");
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("name");
        String imageUrl = token.getPrincipal().getAttribute("picture");

        userRepository.findByProviderUserId(providerUserId)
                .ifPresentOrElse(
                        existingUser -> {
                            existingUser.setImageUrl(imageUrl);
                            
                            userRepository.save(existingUser);
                        },
                        () -> {
                            User newUser = new User();

                            newUser.setProviderUserId(providerUserId);
                            newUser.setName(name);
                            newUser.setEmail(email);
                            newUser.setImageUrl(imageUrl);
                            newUser.setRole(Role.USER);

                            userRepository.save(newUser);
                        });

        response.sendRedirect(clientUrl);
    }
}
