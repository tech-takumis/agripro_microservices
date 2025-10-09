package com.hashjosh.application.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.dto.ApplicationDynamicFieldsDTO;
import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    private  final ObjectMapper objectMapper;
    private final DocumentServiceClient documentServiceClient;
    public ApplicationResponseDto toApplicationResponseDto(Application entity) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(entity.getId());
        dto.setApplicationTypeId(entity.getApplicationType().getId());
        dto.setUserId(entity.getUserId());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setVersion(entity.getVersion());

        List<String> generatedUrl = new ArrayList<>();
        entity.getDocumentId().forEach(document -> generatedUrl.add(documentServiceClient.generatePresignedUrl(document,30)));
        dto.setFileUploads(generatedUrl);
        // map JsonNode into typed DTO
        if (entity.getDynamicFields() != null) {
            try {
                ApplicationDynamicFieldsDTO dynamic =
                        objectMapper.treeToValue(entity.getDynamicFields(), ApplicationDynamicFieldsDTO.class);
                dto.setDynamicFields(dynamic);
            } catch (Exception e) {
                throw new RuntimeException("Failed to map dynamicFields", e);
            }
        }
        return dto;
    }

    public Application toEntity(ApplicationSubmissionDto submission, ApplicationType type, String userId) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dynamicFieldsNode = objectMapper.valueToTree(submission.getFieldValues());

        return Application.builder()
                .applicationType(type)
                .userId(UUID.fromString(userId))
                .documentId(submission.getDocumentIds())
                .dynamicFields(dynamicFieldsNode)  // Now passing JsonNode instead of Map
                .submittedAt(LocalDateTime.now())
                .build();
    }
}
