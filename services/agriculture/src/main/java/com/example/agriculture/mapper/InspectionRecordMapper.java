package com.example.agriculture.mapper;

import com.example.agriculture.dto.InspectionRecordRequest;
import com.example.agriculture.dto.InspectionRecordResponse;
import com.example.agriculture.entity.InspectionRecord;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InspectionRecordMapper {
    public InspectionRecord toVerificationResult(ApplicationSubmittedEvent event) {

    // Validate and map the status to ApplicationStatus
    return InspectionRecord.builder()
            .submissionId(event.getSubmissionId())
            .uploadedBy(event.getUserId())
            .inspectionType(null)
            .report(null)
            .build();
}

    /**
     *
     * @param dto -> the verification request
     * @param saved -> the saved verification
     * @return -> the verification result
     */
    public InspectionRecord updateVerification(InspectionRecordRequest dto, InspectionRecord saved) {
        return InspectionRecord.builder()
                .submissionId(saved.getSubmissionId())
                .uploadedBy(saved.getUploadedBy())
                .inspectionType(dto.inspectionType())
                .report(dto.report())
                .version(saved.getVersion())
                .build();
    }

    public InspectionRecordResponse toVerificationResponse(InspectionRecord inspectionRecord) {
        return new InspectionRecordResponse(
                inspectionRecord.getId(),
                inspectionRecord.getSubmissionId(),
                inspectionRecord.getInspectionType(),
                inspectionRecord.getReport(),
                inspectionRecord.getUpdatedAt(),
                inspectionRecord.getVersion()
        );
    }
}