package com.order.portal.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.*;

import com.order.portal.models.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    Optional<Notification> findById(Long id);
    List<Notification> findByUserIdOrderByDateDesc(Long userId);

    @DeleteQuery
    void deleteByUserId(Long userId);
}