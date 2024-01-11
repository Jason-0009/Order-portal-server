package com.order.portal.websocket;

import java.util.Map;

import org.springframework.stereotype.Component;

import org.springframework.web.socket.WebSocketSession;

@Component
public class StatisticsHandler extends BaseHandler {
    public StatisticsHandler(Map<String, WebSocketSession> sessions) {
        super(sessions);
    }
}