package com.hashjosh.kafkacommon.application;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationReceivedByPcicEvent {
    private UUID submissionId;
    private UUID userId;
    private String verificationStatus; // COMPLETED, REJECTED
    private LocalDateTime receivedAt;
}
