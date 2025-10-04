package com.hashjosh.kafkacommon.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class InspectionScheduledEvent {
    private UUID submissionId;
    private UUID userId;
    private UUID scheduleId;
    private LocalDateTime inspectionDate;
    private LocalDateTime scheduledAt;
}
