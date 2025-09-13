package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationContract(
        UUID eventId,
        String eventType,
        int schemaVersion,
        UUID applicationId,
        @JsonProperty("occurredAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime occurredAt,
        ApplicationDto payload
) {
}
