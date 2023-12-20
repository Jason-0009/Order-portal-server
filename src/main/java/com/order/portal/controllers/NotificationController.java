package com.order.portal.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import com.order.portal.services.NotificationService;

import com.order.portal.models.Notification;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<Notification> retrieveNotifications() {
        return notificationService.retrieveNotifications();
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable String notificationId) {
        this.notificationService.markNotificationAsRead(notificationId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearUserNotifications(@PathVariable String userId) {
        this.notificationService.clearUserNotifications(userId);

        return ResponseEntity.noContent().build();
    }
}
