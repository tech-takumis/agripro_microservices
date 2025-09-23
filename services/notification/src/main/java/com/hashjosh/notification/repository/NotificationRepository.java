package com.hashjosh.notification.repository;

import com.hashjosh.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification<?>, UUID> {
}
