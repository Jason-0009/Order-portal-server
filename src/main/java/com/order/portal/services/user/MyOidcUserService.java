package com.order.portal.services.user;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.stereotype.Service;

import com.order.portal.repositories.UserRepository;

import com.order.portal.models.user.User;

@Service
public class MyOidcUserService extends OidcUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        User user = userRepository.findByEmail(email).orElse(null);

        SimpleGrantedAuthority authority = user != null ?
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()) :
                new SimpleGrantedAuthority("ROLE_ANONYMOUS");

        return new DefaultOidcUser(Collections.singleton(authority),
                oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
    }
}
