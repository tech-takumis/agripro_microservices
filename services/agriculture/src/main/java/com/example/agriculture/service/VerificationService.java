package com.example.agriculture.service;


import com.example.agriculture.clients.ApplicationClient;
import com.example.agriculture.entity.VerificationRecord;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.repository.VerificationRecordRepository;
import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.constant.verification.VerificationRequestDto;
import com.hashjosh.constant.verification.VerificationStatus;
import com.hashjosh.kafkacommon.application.ApplicationForwarded;
import com.hashjosh.kafkacommon.application.ApplicationUnderReviewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class VerificationService {

    private final VerificationRecordRepository verificationRecordRepository;
    private final AgricultureProducer agricultureProducer;
    private final ApplicationClient applicationClient;

    public void applicationReview(UUID submissionId, VerificationRequestDto status) {

        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ApiException.notFound("Verification record not found: " + submissionId));

        if (record.getStatus() != VerificationStatus.PENDING) {
            throw ApiException.badRequest("Application already verified: " + submissionId);
        }
        record.setStatus(VerificationStatus.valueOf(status.getStatus()));

        verificationRecordRepository.save(record);
        agricultureProducer.publishEvent("application-lifecycle",
                new ApplicationUnderReviewEvent(
                        submissionId,
                        record.getUploadedBy(),
                        LocalDateTime.now()
                ));

        log.info("Application {} marked as under review", submissionId);
    }

    public void forwardToPcic(List<UUID> submissionIds) {
        for (UUID submissionId : submissionIds) {
            forwardApplication(submissionId);
        }
    }

    private void forwardApplication(UUID submissionId) {
        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ApiException.notFound("Verification record not found: " + submissionId));

//        if (record.getStatus() != VerificationStatus.COMPLETED) {
//            throw new IllegalStateException("Application not verified: " + submissionId);
//        }
        /* TODO: Lets make a soft delete here instead so that we can keep track of forwarded applications
                so we need to return the application now every provider using the is not deleted field
                to return only not deleted applications
         */
        record.setStatus(VerificationStatus.COMPLETED);
        verificationRecordRepository.save(record);

        agricultureProducer.publishEvent("application-lifecycle",
                ApplicationForwarded.builder()
                        .userId(record.getUploadedBy())
                        .provider("AGRICULTURE")
                        .submissionId(submissionId)
                        .sentAt(LocalDateTime.now())
                        .build()
        );

        log.info("Application {} sent to PCIC", submissionId);
    }


    public List<ApplicationResponseDto> getAllPendingVerifications() {
        List<VerificationRecord> pendingRecords = verificationRecordRepository.findByStatus(VerificationStatus.PENDING);
        return pendingRecords.stream()
                .map(record -> applicationClient.getApplicationById(record.getSubmissionId(), String.valueOf(record.getUploadedBy())))
                .toList();
    }
}
