package com.hashjosh.verification.mapper;

import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.verification.dto.VerificationRequest;
import com.hashjosh.verification.dto.VerificationResponse;
import com.hashjosh.verification.enums.VerificationStatus;
import com.hashjosh.verification.model.VerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationMapper {
    public VerificationResult toVerificationResult(ApplicationContract contract) {
        String status = contract.payload().status();
        VerificationStatus verificationStatus = "SUBMITTED".equals(status) ?
                VerificationStatus.PENDING : VerificationStatus.valueOf(status);

        return VerificationResult.builder()
                .eventId(contract.eventId())
                .applicationId(contract.applicationId())
                .status(verificationStatus)
                .approvedByAdjuster(false)
                .verifiedByUnderwriter(false)
                .inspectionType(null)
                .rejectionReason(null)
                .report(null)
                .version(contract.payload().version())
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
                .status(VerificationStatus.valueOf(dto.status()))
                .approvedByAdjuster(dto.approvedByAdjuster())
                .verifiedByUnderwriter(dto.verifiedByUnderwriter())
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
                verificationResult.getApprovedByAdjuster(),
                verificationResult.getVerifiedByUnderwriter(),
                verificationResult.getInspectionType(),
                verificationResult.getRejectionReason(),
                verificationResult.getReport(),
                verificationResult.getVerifiedAt(),
                verificationResult.getUpdatedAt(),
                verificationResult.getVersion()
        );
    }
}
