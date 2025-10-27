package com.hashjosh.kafkacommon.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
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
    private UUID submissionId;

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("submittedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime submittedAt;

}
