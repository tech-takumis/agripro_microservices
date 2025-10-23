package com.hashjosh.verification.dto;

import com.hashjosh.verification.enums.VerificationStatus;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationResponseDTO {
    private UUID verificationId;
    private UUID submissionId;
    private UUID uploadedBy;
    private String verificationType;
    private String report;
    private VerificationStatus status;
    private String rejectionReason;
}
