package com.hashjosh.kafkacommon.application;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationReceived {
    private UUID submissionId;
    private UUID userId;
    private String provider; // e.g., "PCIC", "AGRICULTURE"
    private String status; // COMPLETED, REJECTED
    private LocalDateTime receivedAt;
}
