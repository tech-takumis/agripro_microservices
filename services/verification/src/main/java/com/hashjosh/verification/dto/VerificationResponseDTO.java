package com.hashjosh.verification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.verification.VerificationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResponseDTO {
    UUID id;
    String applicationName;
    VerificationStatus status;
    boolean isForwarded;
    UUID batchId;
    String batchName;
    UUID userId;
    List<String> fileUploads;
    @JsonProperty("dynamicFields")
    JsonNode jsonDynamicFields;
    LocalDateTime submittedAt;
    LocalDateTime updatedAt;
    Long version;
}
