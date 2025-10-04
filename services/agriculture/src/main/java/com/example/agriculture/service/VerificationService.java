package com.example.agriculture.service;


import com.example.agriculture.clients.ApplicationClient;
import com.example.agriculture.clients.ApplicationResponseDto;
import com.example.agriculture.entity.VerificationRecord;
import com.example.agriculture.enums.VerificationStatus;
import com.example.agriculture.exception.VerificationException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.mapper.VerificationRecordMapper;
import com.example.agriculture.repository.VerificationRecordRepository;
import com.hashjosh.kafkacommon.application.ApplicationSentToPcicEvent;
import com.hashjosh.kafkacommon.application.ApplicationUnderReviewEvent;
import com.hashjosh.kafkacommon.application.VerificationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class VerificationService {

    private final VerificationRecordRepository verificationRecordRepository;
    private final VerificationRecordMapper verificationRecordMapper;
    private final AgricultureProducer agricultureProducer;
    private final ApplicationClient applicationClient;

    public void startReview(UUID submissionId) {

        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new VerificationException("Verification record not found: " + submissionId,HttpStatus.NOT_FOUND.value()));

        agricultureProducer.publishEvent("application-lifecycle",
                new ApplicationUnderReviewEvent(
                        submissionId,
                        record.getUploadedBy(),
                        LocalDateTime.now()
                ));

        log.info("Application {} marked as under review", submissionId);
    }

    public void completeVerification(UUID submissionId, String report, VerificationStatus status, String rejectionReason) {
        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new VerificationException("Verification record not found: " + submissionId,HttpStatus.NOT_FOUND.value()));
        record.setStatus(status);
        record.setReport(report);
        record.setRejectionReason(status == VerificationStatus.REJECTED ? rejectionReason : null);
        verificationRecordRepository.save(record);

        agricultureProducer.publishEvent("application-lifecycle",
                new VerificationCompletedEvent(
                        submissionId,
                        record.getUploadedBy(),
                        record.getVerificationType(),
                        status.name(),
                        record.getReport(),
                        rejectionReason,
                        LocalDateTime.now()
                ));

        log.info("Verification completed for application: {}, status: {}", submissionId, status);
    }

    public void sendToPcic(UUID submissionId) {
        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new VerificationException("Verification record not found: " + submissionId,HttpStatus.NOT_FOUND.value()));

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
}
