package com.hashjosh.kafkacommon.application;

import java.util.UUID;

public record ApplicationDto(
        UUID applicationId,
        UUID applicationTypeId,
        UUID userId,
        String status,
        Long version
) {
}
