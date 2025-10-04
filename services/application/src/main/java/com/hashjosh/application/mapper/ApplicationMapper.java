package com.hashjosh.application.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {


    public ApplicationResponseDto toApplicationResponseDto(Application application) {
        return new ApplicationResponseDto(
            application.getId(),
                application.getApplicationType().getId(),
                application.getUserId(),
                application.getDynamicFields(),
                application.getSubmittedAt(),
                application.getUpdatedAt(),
                application.getVersion()
        );

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
