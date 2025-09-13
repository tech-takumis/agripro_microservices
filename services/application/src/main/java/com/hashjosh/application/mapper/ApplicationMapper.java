package com.hashjosh.application.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.enums.ApplicationStatus;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.kafkacommon.application.ApplicationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    public Application toApplication(ApplicationType applicationType, JsonNode values, String userId) {
        return Application.builder()
                .applicationType(applicationType)
                .userId(UUID.fromString(userId))
                .dynamicFields(values)
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();
    }

    public ApplicationDto toApplicationDto(Application application) {
        return new ApplicationDto(
                application.getId(),
                application.getApplicationType().getId(),
                application.getUserId(),
                application.getStatus().name(),
                application.getVersion()

        );
    }

    public ApplicationResponseDto toApplicationResponseDto(Application application) {
        return new ApplicationResponseDto(
            application.getId(),
                application.getApplicationType().getId(),
                application.getUserId(),
                application.getDynamicFields(),
                application.getStatus().name(),
                application.getSubmittedAt(),
                application.getUpdatedAt(),
                application.getVersion()
        );

    }
}
