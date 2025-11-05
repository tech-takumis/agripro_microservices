package com.hashjosh.application.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.model.Document;
import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.application.dto.submission.ApplicationSubmissionDto;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationType;
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

        ApplicationType applicationType = entity.getType();

        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(entity.getId());
        dto.setApplicationName(applicationType.getName());
        dto.setUserId(entity.getUserId());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setVersion(entity.getVersion());

        List<String> generatedUrl = new ArrayList<>();

        entity.getDocuments().forEach(document -> {
            generatedUrl.add(documentServiceClient
                    .generatePresignedUrl(String.valueOf(entity.getUserId()),document.getDocumentId(),30));
        });


        dto.setFileUploads(generatedUrl);
        dto.setJsonDynamicFields(entity.getDynamicFields());
        return dto;
    }


    public Application toEntity(ApplicationSubmissionDto submission,ApplicationType type) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dynamicFieldsNode = objectMapper.valueToTree(submission.getFieldValues());

        return Application.builder()
                .type(type)
                .userId(submission.getUseId())
                .dynamicFields(dynamicFieldsNode)
                .documents(submission.getDocuments())
                .submittedAt(LocalDateTime.now())
                .build();
    }
}
