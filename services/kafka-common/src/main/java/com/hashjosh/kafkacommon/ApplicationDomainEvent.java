package com.hashjosh.kafkacommon;

import java.util.UUID;

public interface ApplicationDomainEvent {
    UUID getSubmissionId();
    UUID getUserId();
}
