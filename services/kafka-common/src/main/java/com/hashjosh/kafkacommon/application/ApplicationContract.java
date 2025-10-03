package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor // Add this to support serialization
@AllArgsConstructor
@Data
public class ApplicationContract implements ApplicationDomainEvent {

    @JsonProperty("eventId")
    public UUID eventId;

    @JsonProperty("eventType")
    public EventType eventType;

    @JsonProperty("schemaVersion")
    private int schemaVersion;

    @JsonProperty("status")
    private ApplicationStatus status;

    @JsonProperty("applicationId")
    public UUID applicationId;

    @JsonProperty("uploadedBy")
    private UUID uploadedBy;

    @JsonProperty("occurredAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public LocalDateTime occurredAt;

    @JsonProperty("version")
    private Long version;

}