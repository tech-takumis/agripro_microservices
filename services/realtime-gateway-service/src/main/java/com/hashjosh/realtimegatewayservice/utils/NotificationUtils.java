package com.hashjosh.realtimegatewayservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.realtimegatewayservice.entity.Notification;

import java.time.LocalDateTime;

public class NotificationUtils {

    public static Notification createNotification(String recipient, String type, String title, String message) {
        return Notification.<JsonNode>builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .message(message)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Notification createEmailNotification(String recipient, String subject, String message) {
        return createNotification(recipient, "EMAIL", subject, message);
    }

}