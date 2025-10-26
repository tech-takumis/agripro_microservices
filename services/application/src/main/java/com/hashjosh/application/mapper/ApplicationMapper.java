package com.hashjosh.application.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.application.dto.submission.ApplicationSubmissionDto;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.application.model.Batch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    private final DocumentServiceClient documentServiceClient;
    public ApplicationResponseDto toApplicationResponseDto(Application entity) {

        ApplicationType applicationType = entity.getBatch().getApplicationType();

        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(entity.getId());
        dto.setApplicationName(applicationType.getName());
        dto.setBatchId(entity.getBatch().getId());
        dto.setBatchName(entity.getBatch().getName());
        dto.setUserId(entity.getUserId());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setVersion(entity.getVersion());

        List<String> generatedUrl = new ArrayList<>();
        entity.getDocumentId().forEach(document -> generatedUrl.add(documentServiceClient.generatePresignedUrl(document,30)));
        dto.setFileUploads(generatedUrl);
        dto.setJsonDynamicFields(entity.getDynamicFields());
        return dto;
    }


    public Application toEntity(ApplicationSubmissionDto submission, Batch batch) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dynamicFieldsNode = objectMapper.valueToTree(submission.getFieldValues());

        return Application.builder()
                .batch(batch)
                .userId(submission.getUseId())
                .documentId(submission.getDocumentIds())
                .dynamicFields(dynamicFieldsNode)  // Now passing JsonNode instead of Map
                .submittedAt(LocalDateTime.now())
                .build();
    }
}
