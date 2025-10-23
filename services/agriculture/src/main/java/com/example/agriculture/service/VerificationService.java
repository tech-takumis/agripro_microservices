package com.example.agriculture.service;


import com.hashjosh.constant.verification.VerificationRequestDto;
import com.example.agriculture.entity.VerificationRecord;
import com.hashjosh.constant.verification.VerificationStatus;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.repository.VerificationRecordRepository;
import com.hashjosh.kafkacommon.application.ApplicationSentToPcicEvent;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import com.hashjosh.kafkacommon.application.ApplicationUnderReviewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class VerificationService {

    private final VerificationRecordRepository verificationRecordRepository;
    private final AgricultureProducer agricultureProducer;

    public void startReview(UUID submissionId) {

        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ApiException.notFound("Verification record not found: " + submissionId));

        agricultureProducer.publishEvent("application-lifecycle",
                new ApplicationUnderReviewEvent(
                        submissionId,
                        record.getUploadedBy(),
                        LocalDateTime.now()
                ));

        log.info("Application {} marked as under review", submissionId);
    }

    public void sendToPcic(UUID submissionId) {
        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ApiException.notFound("Verification record not found: " + submissionId));

        if (record.getStatus() != VerificationStatus.COMPLETED) {
            throw new IllegalStateException("Application not verified: " + submissionId);
        }

        agricultureProducer.publishEvent("application-lifecycle",
                new ApplicationSentToPcicEvent(
                        submissionId,
                        record.getUploadedBy(),
                        LocalDateTime.now()
                ));

        log.info("Application {} sent to PCIC", submissionId);
    }

    public void createVerificationRecord(
            VerificationRequestDto dto
    ) {
        log.info("Creating new verification record");
        VerificationRecord record = VerificationRecord.builder()
                .submissionId(dto.getSubmissionId())
                .uploadedBy(dto.getUploadedBy())
                .verificationType("Application Verification")
                .status(VerificationStatus.PENDING)
                .build();

        agricultureProducer.publishEvent("application-lifecycle",
                ApplicationSubmittedEvent.builder()
                        .submissionId(dto.getSubmissionId())
                        .userId(UUID.fromString(String.valueOf(dto.getUploadedBy())))
                        .submittedAt(LocalDateTime.now())
                        .build()
        );

        verificationRecordRepository.save(record);

    }
}
