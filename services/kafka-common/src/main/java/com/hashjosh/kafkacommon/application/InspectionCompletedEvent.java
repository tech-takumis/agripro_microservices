package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class InspectionCompletedEvent implements ApplicationDomainEvent {
    @JsonProperty("submissionId")
    private UUID submissionId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("applicationTypeId")
    private String inspectionType;

    @JsonProperty("report")
    private String report;

    @JsonProperty("uploadedBy")
    private UUID uploadedBy;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;


}
