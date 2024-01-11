package com.order.portal.repositories;

import java.util.List;

import lombok.NonNull;

import org.springframework.data.domain.Sort;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.order.portal.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    @NonNull List<Notification> findAll(@NonNull Sort sort);
    void deleteByUserId(String userId);
}