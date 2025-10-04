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
public class VerificationCompletedEvent implements ApplicationDomainEvent {
    private UUID submissionId;
    private UUID userId;
    private String verificationType;
    private String report;
    private String status; // COMPLETED, REJECTED
    private String rejectionReason;
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