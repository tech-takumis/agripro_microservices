package com.hashjosh.kafkacommon.application;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApplicationForwarded {
    private UUID submissionId;
    private UUID userId;
    private String provider;
    private LocalDateTime sentAt;
}

