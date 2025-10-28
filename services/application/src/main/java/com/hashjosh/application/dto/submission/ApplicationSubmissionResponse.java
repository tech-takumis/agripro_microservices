package com.hashjosh.application.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSubmissionResponse {
    private boolean success;
    private String message;
    private UUID applicationId;
}