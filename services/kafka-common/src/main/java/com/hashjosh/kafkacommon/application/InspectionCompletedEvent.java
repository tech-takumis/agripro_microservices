package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashjosh.kafkacommon.ApplicationDomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InspectionCompletedEvent {
    @JsonProperty("submissionId")
    private UUID submissionId;
    @JsonProperty("userID")
    private UUID userId;
    @JsonProperty("status")
    private String status; // COMPLETED, INVALID
    @JsonProperty("comments")
    private String comments;
    @JsonProperty("inspectedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime inspectedAt;

}
