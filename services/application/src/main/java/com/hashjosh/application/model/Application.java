package com.hashjosh.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.enums.ApplicationStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "applications")
public class Application implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonProperty("id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_type_id", nullable = false)
    @JsonIgnore
    @JsonProperty("applicationTypeId")
    private ApplicationType applicationType;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private UUID userId;

    @Type(JsonBinaryType.class)
    @Column(name = "dynamic_fields", columnDefinition = "jsonb", nullable = false)
    @JsonProperty("dynamicFields")
    private JsonNode dynamicFields;

    // Current status only (delegated to Workflow Service for full history)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    @JsonProperty("status")
    private ApplicationStatus status; // SUBMITTED, VERIFIED, REJECTED

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @JsonProperty("version")
    private Long version; // For optimistic locking

}