package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
public class ApplicationSubmissionContract {
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

    @JsonProperty("occurredAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime occurredAt;

    @JsonProperty("version")
    private Long version;
}
