package com.hashjosh.document.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record DocumentRequest(
        @RequestPart("referenceId") UUID referenceId,
        @RequestPart("file") MultipartFile file,
        @RequestPart("documentType") String documentType,
        @RequestPart(value = "metaData", required = false) JsonNode metaData
) {
}
