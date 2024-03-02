package com.order.portal.models.user;

import lombok.Data;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    @JsonIgnore
    private String uuid;
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String imageUrl;
    private String preferredLanguage;

    public static final String SEQUENCE_NAME = "users_sequence";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
