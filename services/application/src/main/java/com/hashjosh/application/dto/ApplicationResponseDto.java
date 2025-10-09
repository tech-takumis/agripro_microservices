package com.hashjosh.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ApplicationResponseDto{
    UUID id;
    UUID applicationTypeId;
    UUID userId;
    List<String> fileUploads;
    @JsonProperty("dynamicFields")
    ApplicationDynamicFieldsDTO dynamicFields;
    LocalDateTime  submittedAt;
    LocalDateTime updatedAt;
    Long version;
}
