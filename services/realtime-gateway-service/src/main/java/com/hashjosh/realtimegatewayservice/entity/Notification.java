package com.hashjosh.realtimegatewayservice.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recipient", nullable = false)
    private String recipient; // Could be a user ID, email, etc.

    @Column(name = "type", nullable = false, length = 50)
    private String type; // Type of notification (e.g., EMAIL, SMS, PUSH)

    @Column(name = "status", length = 20)
    private String status; // e.g., PENDING, SENT, FAILED

    @Column(name = "is_read")
    private boolean read;

    @Column(name = "title", nullable = false, length = 100)
    private String title; // Title or subject of the notification

    @Column(name = "massage")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
