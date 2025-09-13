package com.hashjosh.application.dto;

import java.util.UUID;

public record DocumentResponse(
        UUID documentId,
        UUID applicationId,
        UUID uploadedBy,
        String fileName,
        String fileType,
        String objectKey,
        String uploadedAt
) {
}
