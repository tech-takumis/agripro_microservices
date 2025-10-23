package com.hashjosh.application.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.dto.ApplicationDynamicFieldsDTO;
import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.application.model.Document;
import com.hashjosh.constant.document.dto.DocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    private  final ObjectMapper objectMapper;
    private final DocumentServiceClient documentServiceClient;
    public ApplicationResponseDto toApplicationResponseDto(Application entity) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(entity.getId());
        dto.setApplicationTypeId(entity.getApplicationType().getId());
        dto.setUserId(entity.getUploadedBy());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setVersion(entity.getVersion());

        List<String> generatedUrl = new ArrayList<>();
        entity.getDocumentsUploaded().forEach(document -> generatedUrl.add(documentServiceClient.generatePresignedUrl(document.getDocumentId(),30)) );
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


    public Application toEntity(ApplicationSubmissionDto submission, ApplicationType type, Set<Document> documents) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dynamicFieldsNode = objectMapper.valueToTree(submission.getFieldValues());

        return Application.builder()
                .applicationType(type)
                .uploadedBy(submission.getUploadedBy())
                .documentsUploaded(documents)
                .dynamicFields(dynamicFieldsNode)  // Now passing JsonNode instead of Map
                .submittedAt(LocalDateTime.now())
                .build();
    }

    public Document toDocumentEntity(DocumentResponse response) {
        return Document.builder()
                .uploadedBy(response.getUploadedBy())
                .documentId(response.getDocumentId())
                .build();
    }
}
