package com.hashjosh.application.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public record ApplicationSubmissionDto(
        JsonNode fieldValues,
        Map<String, MultipartFile> files
        ) {
}
