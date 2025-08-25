package com.hashjosh.applicationservice.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.applicationservice.model.Application;
import com.hashjosh.applicationservice.model.ApplicationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    public Application toApplication(ApplicationType applicationType, JsonNode values, Long userId) {
        return Application.builder()
                .applicationType(applicationType)
                .userId(userId)
                .dynamicFields(values)
                .status("Submitted")
                .submittedAt(LocalDateTime.now())
                .build();
    }
}
