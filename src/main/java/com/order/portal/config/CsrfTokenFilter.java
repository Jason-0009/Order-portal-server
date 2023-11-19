package com.order.portal.config;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CsrfTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            filterChain.doFilter(request, response);

            return;
        }

        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrf == null) {
            filterChain.doFilter(request, response);

            return;
        }

        Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
        String token = csrf.getToken();

        if (cookie != null && token != null && token.equals(cookie.getValue())) {
            filterChain.doFilter(request, response);

            return;
        }

        cookie = createCsrfCookie(token);

        response.addCookie(cookie);

        filterChain.doFilter(request, response);
    }

    private Cookie createCsrfCookie(String token) {
        Cookie cookie = new Cookie("XSRF-TOKEN", token);

        cookie.setPath("/");

        return cookie;
    }
}
