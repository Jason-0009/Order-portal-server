package com.order.portal.services;

import java.io.IOException;

import java.time.Instant;

import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.order.portal.models.Notification;
import com.order.portal.models.OAuthAccount;

import com.order.portal.repositories.NotificationRepository;

import com.order.portal.websocket.NotificationHandler;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final SequenceGeneratorService sequenceGeneratorService;

    private final NotificationHandler notificationHandler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public List<Notification> retrieveNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByDateDesc(userId);
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification = retrieveNotificationById(notificationId);

        notification.setReadStatus(true);

        notificationRepository.save(notification);
    }

    public void clearNotifications(Long userId) {
        notificationRepository.deleteByUserId(userId);
    }

    public void saveNotification(OAuthAccount oauthAccount, String messageCode, String redirectUrl) throws IOException {
        Notification notification = new Notification();

        notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
        notification.setUserId(oauthAccount.getUserId());
        notification.setMessageCode(messageCode);
        notification.setDate(Instant.now());
        notification.setRedirectUrl(redirectUrl);

        notificationRepository.save(notification);

        String notificationJson = objectMapper.writeValueAsString(notification);

        notificationHandler.sendMessage(oauthAccount.getOauthUserId(), notificationJson);
    }

    private Notification retrieveNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notification not found."));
    }
}
