package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.constant.application.RecipientType;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ApplicationSubmittedEvent  {
    @JsonProperty("submissionId")
    private UUID submissionId; // application id

    @JsonProperty("recipientType")
    private RecipientType recipientType;

    @JsonProperty("uploadedBy")
    private UUID uploadedBY;

    @JsonProperty("applicationTypeId")
    private UUID applicationTypeId;

    @JsonProperty("dynamicFields")
    private JsonNode dynamicFields;

    @JsonProperty("documentIds")
    private List<UUID> documentIds;

    @JsonProperty("submittedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime submittedAt;

}
