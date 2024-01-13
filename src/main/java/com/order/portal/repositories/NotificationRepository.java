package com.order.portal.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.order.portal.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByDateAsc(String userId);

    @DeleteQuery
    void deleteByUserId(String userId);
}