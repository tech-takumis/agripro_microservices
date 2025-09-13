package com.hashjosh.document.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record DocumentRequest(
        UUID applicationId,
        UUID uploadedBy,
        MultipartFile file
) {
}
