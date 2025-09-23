package com.hashjosh.notification.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a generic notification entity suitable for multiple use cases.
 * The payload of the notification is generic to allow flexibility.
 *
 * @param <T> The type of the payload (dynamic content of the notification).
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recipient", nullable = false)
    private String recipient; // Could be a user ID, email, etc.

    @Column(name = "type", nullable = false, length = 50)
    private String type; // Type of notification (e.g., EMAIL, SMS, PUSH)

    @Column(name = "status", length = 20)
    private String status; // e.g., PENDING, SENT, FAILED

    @Column(name = "title", nullable = false, length = 100)
    private String title; // Title or subject of the notification

    @Type(JsonBinaryType.class)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private JsonNode payload; // JSON representation of dynamic content

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "template", length = 50)
    private String template; // For templated notifications (optional)
}