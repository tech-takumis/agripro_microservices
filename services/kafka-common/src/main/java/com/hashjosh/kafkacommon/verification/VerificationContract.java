package com.hashjosh.kafkacommon.verification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationContract(
        UUID eventId,
        String eventType,
        int schemaVersion,
        @JsonProperty("occurredAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime occurredAt,
        VerificationDto payload
) {
}
