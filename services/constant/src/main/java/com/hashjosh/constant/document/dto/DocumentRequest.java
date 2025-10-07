package com.hashjosh.constant.document.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.program.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record DocumentRequest(
        MultipartFile file,
        DocumentType documentType,
        JsonNode metaData
) {
}
