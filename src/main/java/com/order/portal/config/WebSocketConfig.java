package com.order.portal.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.order.portal.websocket.NotificationHandler;
import com.order.portal.websocket.StatisticsHandler;
import com.order.portal.websocket.OrderHandler;
import com.order.portal.websocket.UserHandler;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final NotificationHandler notificationHandler;
    private final StatisticsHandler statisticsHandler;
    private final OrderHandler orderHandler;
    private final UserHandler userHandler;

    @Value("${client.url}")
    private String clientUrl;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(notificationHandler, "/notifications")
                .addHandler(statisticsHandler, "/statistics")
                .addHandler(orderHandler, "/orders")
                .addHandler(userHandler, "/users")
                .setAllowedOrigins(clientUrl);
    }
}
