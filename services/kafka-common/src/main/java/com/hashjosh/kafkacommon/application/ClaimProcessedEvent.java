package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ClaimProcessedEvent implements ApplicationDomainEvent {
    @JsonProperty("submissionId")
    private UUID submissionId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("claimId")
    private UUID claimId;

    @JsonProperty("payoutStatus")
    private String payoutStatus;

    @JsonProperty("claimAmount")
    private Double claimAmount;

    @JsonProperty("processedAt")
    private LocalDateTime processedAt;

}
