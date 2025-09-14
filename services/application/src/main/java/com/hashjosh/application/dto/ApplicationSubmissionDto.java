package com.hashjosh.application.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public record ApplicationSubmissionDto(
        JsonNode fieldValues,
        Map<String, MultipartFile> files
) {
        // Constructor to handle String input for fieldValues
        public ApplicationSubmissionDto(String fieldValues, Map<String, MultipartFile> files) {
                this(convertToJsonNode(fieldValues), files);
        }

        private static JsonNode convertToJsonNode(String fieldValues) {
                if (fieldValues == null || fieldValues.trim().isEmpty()) {
                        throw new IllegalArgumentException("Field values cannot be null or empty");
                }
                try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(fieldValues);
                        if (!node.isObject()) {
                                throw new IllegalArgumentException("Field values must be a valid JSON object");
                        }
                        return node;
                } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid JSON format for fieldValues: " + e.getMessage(), e);
                }
        }
}
