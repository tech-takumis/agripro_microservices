package com.hashjosh.verification.mapper;

import com.hashjosh.verification.dto.VerificationRequestDTO;
import com.hashjosh.verification.dto.VerificationResponseDTO;
import com.hashjosh.verification.entity.VerificationRecord;
import org.springframework.stereotype.Component;

@Component
public class VerificationMapper {

    public VerificationRecord toEntity(VerificationRequestDTO dto) {
        return VerificationRecord.builder()
                .submissionId(dto.getSubmissionId())
                .uploadedBy(dto.getUploadedBy())
                .verificationType(dto.getVerificationType())
                .report(dto.getReport())
                .status(dto.getStatus())
                .rejectionReason(dto.getRejectionReason())
                .build();
    }

    public VerificationResponseDTO toResponseDto(VerificationRecord entity) {
        return VerificationResponseDTO.builder()
                .verificationId(entity.getId())
                .submissionId(entity.getSubmissionId())
                .uploadedBy(entity.getUploadedBy())
                .verificationType(entity.getVerificationType())
                .report(entity.getReport())
                .status(entity.getStatus())
                .rejectionReason(entity.getRejectionReason())
                .build();
    }

    public void updateEntityFromDto(VerificationRequestDTO dto, VerificationRecord entity) {
        entity.setVerificationType(dto.getVerificationType());
        entity.setReport(dto.getReport());
        entity.setStatus(dto.getStatus());
        entity.setRejectionReason(dto.getRejectionReason());
    }
}