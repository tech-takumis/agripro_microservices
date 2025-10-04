package com.hashjosh.kafkacommon;

import com.hashjosh.constant.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ApplicationDomainEvent {
    UUID getSubmissionId();
    UUID getUserId();
}
