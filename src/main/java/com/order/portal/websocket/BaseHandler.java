package com.order.portal.websocket;

import java.util.Map;

import java.io.IOException;

import java.security.Principal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@RequiredArgsConstructor
public abstract class BaseHandler extends AbstractWebSocketHandler {
    protected final Map<String, WebSocketSession> sessions;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Principal principal = session.getPrincipal();

        if (!(principal instanceof OAuth2AuthenticationToken token)) return;

        String oauthUserId = token.getPrincipal().getAttribute("sub");

        sessions.put(oauthUserId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        Principal principal = session.getPrincipal();

        if (!(principal instanceof OAuth2AuthenticationToken token)) return;

        String oauthUserId = token.getPrincipal().getAttribute("sub");

        sessions.remove(oauthUserId);
    }

    public void sendMessage(String oauthUserId, String message) throws IOException {
        WebSocketSession session = sessions.get(oauthUserId);

        if (session == null || !session.isOpen()) return;

        session.sendMessage(new TextMessage(message));
    }
}