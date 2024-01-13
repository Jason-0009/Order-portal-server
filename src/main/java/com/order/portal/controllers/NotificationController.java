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
    public List<Notification> retrieveNotifications(@PathVariable String userId) {
        return notificationService.retrieveNotifications(userId);
    }

    @PutMapping("/{notificationId}")
    public void markNotificationAsRead(@PathVariable String notificationId) {
        this.notificationService.markNotificationAsRead(notificationId);
    }

    @DeleteMapping("/{userId}")
    public void clearUserNotifications(@PathVariable String userId) {
        this.notificationService.clearNotifications(userId);
    }
}
