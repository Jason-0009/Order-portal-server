package com.order.portal.config;

import java.net.URI;

import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.*;

import org.springframework.security.web.csrf.*;

@Configuration
public class CsrfTokenConfig {

    @Value("${client.url}")
    private String clientUrl;

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new CsrfTokenRepository() {
            private final HttpSessionCsrfTokenRepository delegate = new HttpSessionCsrfTokenRepository();

            @Override
            public CsrfToken generateToken(HttpServletRequest request) {
                return delegate.generateToken(request);
            }

            @Override
            public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
                String tokenValue = token == null ? "" : token.getToken();
                Cookie cookie = new Cookie("X-CSRF-TOKEN", tokenValue);

                cookie.setSecure(request.isSecure());
                cookie.setPath(request.getContextPath());
                cookie.setMaxAge(-1);

                if (token == null)
                    cookie.setMaxAge(0);


                cookie.setHttpOnly(true);

                URI clientUri = URI.create(clientUrl);

                String domain = clientUri.getHost().startsWith("www.") ?
                        clientUri.getHost().substring(4) : clientUri.getHost();
                cookie.setDomain(domain);

                response.addCookie(cookie);
            }

            @Override
            public CsrfToken loadToken(HttpServletRequest request) {
                return delegate.loadToken(request);
            }
        };
    }
}
