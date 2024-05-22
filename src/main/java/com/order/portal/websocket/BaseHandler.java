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
        String oauthUserId = getOauthUserId(session.getPrincipal());
        if (oauthUserId == null) return;

        sessions.put(oauthUserId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        String oauthUserId = getOauthUserId(session.getPrincipal());
        if (oauthUserId == null) return;

        sessions.remove(oauthUserId);
    }

    public void sendMessage(String oauthUserId, String message) throws IOException {
        WebSocketSession session = sessions.get(oauthUserId);
        if (session == null || !session.isOpen()) return;

        session.sendMessage(new TextMessage(message));
    }

    public void broadcastMessage(String message) {
        sessions.values().forEach(session -> {
            if (!session.isOpen()) return;

            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private String getOauthUserId(Principal principal) {
        if (!(principal instanceof OAuth2AuthenticationToken token)) return null;

        return token.getPrincipal().getAttribute("sub");
    }
}