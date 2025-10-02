package com.hashjosh.verification.mapper;

import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import com.hashjosh.verification.dto.VerificationRequest;
import com.hashjosh.verification.dto.VerificationResponse;
import com.hashjosh.verification.model.VerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationMapper {
    public VerificationResult toVerificationResult(ApplicationSubmissionContract contract) {

    // Validate and map the status to ApplicationStatus
    return VerificationResult.builder()
            .eventId(contract.getEventId())
            .applicationId(contract.getApplicationId())
            .uploadedBy(contract.getUploadedBy())
            .status(contract.getStatus())
            .inspectionType(null)
            .rejectionReason(null)
            .report(null)
            .version(contract.getVersion())
            .build();
}

    /**
     *
     * @param dto -> the verification request
     * @param saved -> the saved verification
     * @return -> the verification result
     */
    public VerificationResult updateVerification(VerificationRequest dto, VerificationResult saved) {
        return VerificationResult.builder()
                .id(saved.getId())
                .eventId(saved.getEventId())
                .applicationId(saved.getApplicationId())
                .status(ApplicationStatus.valueOf(dto.status()))
                .inspectionType(dto.inspectionType())
                .rejectionReason(dto.rejectionReason())
                .report(dto.report())
                .version(saved.getVersion())
                .build();
    }

    public VerificationResponse toVerificationResponse(VerificationResult verificationResult) {
        return new VerificationResponse(
            verificationResult.getId(),
                verificationResult.getApplicationId(),
                verificationResult.getStatus().name(),
                verificationResult.getInspectionType(),
                verificationResult.getRejectionReason(),
                verificationResult.getReport(),
                verificationResult.getVerifiedAt(),
                verificationResult.getUpdatedAt(),
                verificationResult.getVersion()
        );
    }
}