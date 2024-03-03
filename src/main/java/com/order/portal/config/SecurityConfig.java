package com.order.portal.config;

import java.net.URI;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.*;

import org.springframework.context.annotation.*;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.web.cors.*;

import com.order.portal.config.handlers.OAuth2LoginSuccessHandler;

import com.order.portal.services.user.MyOidcUserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    private final MyOidcUserService myOidcUserService;

    @Value("${client.url}")
    private String clientUrl;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        URI clientUri = new URI(clientUrl);
        String domain = clientUri.getHost().startsWith("www.") ?
                clientUri.getHost().substring(4) : clientUri.getHost();

        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

        csrfTokenRepository.setCookieCustomizer(cookie -> cookie.domain(domain));

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .addFilterAfter(new CookieCsrfFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/products", "/api/orders").authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2.successHandler(oauth2LoginSuccessHandler)
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.oidcUserService(myOidcUserService)))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl(clientUrl));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin(clientUrl);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
