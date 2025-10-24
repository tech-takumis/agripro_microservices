package com.hashjosh.realtimegatewayservice.repository;

import com.hashjosh.realtimegatewayservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientOrRecipient(String all, String username);
}
