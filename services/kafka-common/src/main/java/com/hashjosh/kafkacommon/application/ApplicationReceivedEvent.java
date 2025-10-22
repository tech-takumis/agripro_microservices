package com.hashjosh.kafkacommon.application;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationReceivedEvent {
    private UUID submissionId;
    private UUID uploadedBY;
    private String verificationStatus;
    private String detials;
    private LocalDateTime receivedAt;
}
