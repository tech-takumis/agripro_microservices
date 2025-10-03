package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PolicyIssuedEvent implements ApplicationDomainEvent {
    @JsonProperty("submissionId")
    private UUID submissionId;

    @JsonProperty("userId")
    private UUID userId;


    @JsonProperty("policyId")
    private UUID policyId;

    @JsonProperty("policyNumber")
    private String policyNumber;

    @JsonProperty("coverageAmount")
    private Double coverageAmount;

    @JsonProperty("issuedAt")
    private LocalDateTime issuedAt;
}

/*
PolicyIssuedEvent {
  "submissionId": UUID, // Application.id
  "userId": UUID,
  "policyId": UUID,
  "policyNumber": String,
  "coverageAmount": Double,
  "issuedAt": Timestamp
}
 */
