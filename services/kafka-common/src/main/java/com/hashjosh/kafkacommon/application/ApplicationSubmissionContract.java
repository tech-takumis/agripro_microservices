package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;  // Add this annotation
import lombok.AllArgsConstructor; // Add this annotation

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSubmissionContract implements ApplicationDomainEvent {
    @JsonProperty("eventId")
    private UUID eventId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("eventType")
    private EventType eventType;

    @JsonProperty("schemaVersion")
    private int schemaVersion;

    @JsonProperty("status")
    private ApplicationStatus status;

    @JsonProperty("applicationId")
    private UUID applicationId;

    @JsonProperty("uploadedBy")
    private UUID uploadedBy;

    @JsonProperty("gmail")
    private String gmail;

    @JsonProperty("occurredAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime occurredAt;

    @JsonProperty("version")
    private Long version;
}