package com.hashjosh.realtimegatewayservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hashjosh.realtimegatewayservice.dto.EmailNotificationPayload;
import com.hashjosh.realtimegatewayservice.dto.SMSNotificationPayload;
import com.hashjosh.realtimegatewayservice.entity.Notification;

import java.time.LocalDateTime;

public class NotificationUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a new notification with the given parameters
     * @param recipient The recipient of the notification
     * @param type The type of notification (e.g., EMAIL, SMS)
     * @param title The title of the notification
     * @param payload The payload as a JsonNode
     * @return A new Notification instance
     */
    public static Notification createNotification(String recipient,
                                                            String type, String title,
                                                            JsonNode payload) {
        return Notification.<JsonNode>builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .status("PENDING")
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a new email notification
     * @param recipient The email recipient
     * @param subject The email subject
     * @param message The email message
     * @param attachmentUrl Optional attachment URL
     * @return A new Notification instance with email payload
     */
    public static Notification createEmailNotification(String recipient, String subject, String message, String attachmentUrl) {
        EmailNotificationPayload emailPayload = new EmailNotificationPayload(subject, message, attachmentUrl);
        JsonNode payloadNode = objectMapper.valueToTree(emailPayload);
        return createNotification(recipient, "EMAIL", subject, payloadNode);
    }

    /**
     * Creates a new SMS notification
     * @param phoneNumber The recipient's phone number
     * @param message The SMS message
     * @return A new Notification instance with SMS payload
     */
    public static Notification createSMSNotification(String phoneNumber, String message) {
        SMSNotificationPayload smsPayload = new SMSNotificationPayload(phoneNumber, message);
        JsonNode payloadNode = objectMapper.valueToTree(smsPayload);
        return createNotification(phoneNumber, "SMS", "SMS Notification", payloadNode);
    }

    /**
     * Converts a notification's payload to the specified type
     * @param notification The notification containing the payload
     * @param valueType The target class type
     * @param <T> The type to convert the payload to
     * @return The deserialized payload object
     * @throws JsonProcessingException if the payload cannot be deserialized
     */
    public static <T> T getPayloadAs(Notification notification, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.treeToValue(notification.getPayload(), valueType);
    }

    /**
     * Updates a notification's payload with new values
     * @param notification The notification to update
     * @param updates A JsonNode containing the fields to update
     * @return The updated notification
     */
    public static Notification updateNotificationPayload(Notification notification, JsonNode updates) {
        if (updates == null || !updates.isObject()) {
            return notification;
        }

        ObjectNode payload = notification.getPayload().deepCopy();
        updates.fields().forEachRemaining(entry -> {
            payload.set(entry.getKey(), entry.getValue());
        });

        notification.setPayload(payload);
        return notification;
    }


}