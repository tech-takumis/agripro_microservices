package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimProcessedEvent{
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
