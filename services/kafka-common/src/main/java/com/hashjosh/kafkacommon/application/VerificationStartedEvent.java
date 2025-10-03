package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class VerificationStartedEvent implements ApplicationDomainEvent {
    @JsonProperty("submissionId")
    private UUID submissionId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("startedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startedAt;

}
