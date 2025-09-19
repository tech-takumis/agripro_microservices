package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor // Add this to support serialization
@Data
public class ApplicationContract {

    private UUID eventId;

    @JsonProperty("eventType")
    private String eventType;

    private UUID applicationId;

    @JsonProperty("occurredAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime occurredAt;

    private ApplicationPayload payload;

    // Builder annotation should be retained if used elsewhere.
    @Builder
    public ApplicationContract(UUID eventId, String eventType, UUID applicationId, LocalDateTime occurredAt, ApplicationPayload payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.applicationId = applicationId;
        this.occurredAt = occurredAt;
        this.payload = payload;
    }
}