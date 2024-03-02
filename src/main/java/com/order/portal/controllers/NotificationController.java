package com.order.portal.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.order.portal.services.NotificationService;

import com.order.portal.models.Notification;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> retrieveNotifications(@PathVariable Long userId) {
        return notificationService.retrieveNotifications(userId);
    }

    @PutMapping("/{notificationId}")
    public void markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }

    @DeleteMapping("/{userId}")
    public void clearUserNotifications(@PathVariable Long userId) {
        notificationService.clearNotifications(userId);
    }
}
