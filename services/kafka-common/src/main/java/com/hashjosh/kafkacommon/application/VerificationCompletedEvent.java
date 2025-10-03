package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class VerificationCompletedEvent implements ApplicationDomainEvent {
    @JsonProperty("submissionId")
    private UUID submissionId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("rejectionReason")
    private String rejectionReason;

    @JsonProperty("verifiedAt")
    private LocalDateTime verifiedAt;

}

/*
* VerificationCompletedEvent {
  "submissionId": UUID, // Application.id
  "userId": UUID,
  "status": "PENDING|APPROVED|REJECTED",
  "rejectionReason": String (optional),
  "verifiedAt": Timestamp
}
*
**/