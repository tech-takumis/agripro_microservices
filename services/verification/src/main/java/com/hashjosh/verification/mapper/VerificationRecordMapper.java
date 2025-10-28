package com.hashjosh.verification.mapper;

import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.verification.dto.VerificationResponseDTO;
import com.hashjosh.verification.entity.VerificationRecord;
import org.springframework.stereotype.Component;

@Component
public class VerificationRecordMapper {

    public VerificationResponseDTO toVerificationResponseDTO(VerificationRecord record, ApplicationResponseDto app) {
        return VerificationResponseDTO.builder()
                .id(record.getSubmissionId())
                .applicationName(app != null ? app.getApplicationName() : null)
                .status(record.getStatus())
                .isForwarded(record.isForwarded())
                .batchId(record.getBatch().getId())
                .batchName(record.getBatch().getName())
                .userId(app != null ? app.getUserId() : record.getUploadedBy())
                .fileUploads(app != null ? app.getFileUploads() : null)
                .jsonDynamicFields(app != null ? app.getJsonDynamicFields() : null)
                .submittedAt(app != null ? app.getSubmittedAt() : null)
                .updatedAt(app != null ? app.getUpdatedAt() : null)
                .version(app != null ? app.getVersion() : record.getVersion())
                .build();
    }
}
