package com.order.portal.config;

import java.io.IOException;

import org.springframework.lang.NonNull;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.*;

import jakarta.servlet.http.*;

public class CsrfTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            filterChain.doFilter(request, response);

            return;
        }

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken == null) {
            filterChain.doFilter(request, response);

            return;
        }

        Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
        String token = csrfToken.getToken();

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
