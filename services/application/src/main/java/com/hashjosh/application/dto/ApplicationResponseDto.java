package com.hashjosh.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDto{
    UUID id;
    String applicationName;
    UUID batchId;
    String batchName;
    UUID userId;
    List<String> fileUploads;
    @JsonProperty("dynamicFields")
//    ApplicationDynamicFieldsDTO dynamicFields;
    JsonNode jsonDynamicFields;
    LocalDateTime  submittedAt;
    LocalDateTime updatedAt;
    Long version;
}
