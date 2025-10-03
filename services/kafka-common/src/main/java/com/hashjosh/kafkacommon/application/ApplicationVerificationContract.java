package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor  // Add this annotation
@AllArgsConstructor // Add this annotation
public class ApplicationVerificationContract implements ApplicationDomainEvent {
    @JsonProperty("eventId")
    private UUID eventId;

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

    @JsonProperty("occurredAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime occurredAt;

    @JsonProperty("version")
    private Long version;
}
