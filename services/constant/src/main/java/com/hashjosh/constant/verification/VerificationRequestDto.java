package com.hashjosh.constant.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequestDto {
    private UUID submissionId;
    private UUID uploadedBy;
    private String report;
    private VerificationStatus status;
    private String rejectionReason;
}
