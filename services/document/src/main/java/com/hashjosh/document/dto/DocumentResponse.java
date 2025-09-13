package com.hashjosh.document.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID documentId,
        UUID applicationId,
        UUID uploadedBy,
        String fileName,
        String fileType,
        String objectKey,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        LocalDateTime uploadedAt
) {
}
