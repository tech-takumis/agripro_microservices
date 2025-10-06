package com.hashjosh.constant.document.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record DocumentRequest(
        UUID referenceId,
        MultipartFile file,
        String documentType,
        JsonNode metaData
) {
}
