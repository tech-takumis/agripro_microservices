package com.hashjosh.verification.service;

import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.constant.verification.VerificationRequestDto;
import com.hashjosh.constant.verification.VerificationStatus;
import com.hashjosh.kafkacommon.application.ApplicationForwarded;
import com.hashjosh.kafkacommon.application.ApplicationUnderReviewEvent;
import com.hashjosh.verification.clients.ApplicationClient;
import com.hashjosh.verification.dto.VerificationResponseDTO;
import com.hashjosh.verification.entity.Batch;
import com.hashjosh.verification.entity.VerificationRecord;
import com.hashjosh.verification.exception.ApiException;
import com.hashjosh.verification.kafka.VerificationProducer;
import com.hashjosh.verification.mapper.VerificationRecordMapper;
import com.hashjosh.verification.repository.BatchRepository;
import com.hashjosh.verification.repository.VerificationRecordRepository;
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
    private final VerificationProducer publisher;
    private final ApplicationClient applicationClient;
    private final VerificationRecordMapper verificationRecordMapper;
    private final BatchRepository batchRepository;

    public void applicationReview(UUID submissionId, VerificationRequestDto status) {

        VerificationRecord record = verificationRecordRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ApiException.notFound("Verification record not found: " + submissionId));

        if (record.getStatus() != VerificationStatus.PENDING) {
            throw ApiException.badRequest("Application already verified: " + submissionId);
        }
        record.setStatus(VerificationStatus.valueOf(status.getStatus()));

        verificationRecordRepository.save(record);
        publisher.publishEvent("application-lifecycle",
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


        record.setStatus(VerificationStatus.COMPLETED);
        record.setForwarded(true);
        record.getBatch().setMaxApplications(record.getBatch().getMaxApplications() - 1);
        verificationRecordRepository.save(record);

        publisher.publishEvent("application-lifecycle",
                ApplicationForwarded.builder()
                        .userId(record.getUploadedBy())
                        .provider("AGRICULTURE")
                        .submissionId(submissionId)
                        .sentAt(LocalDateTime.now())
                        .build()
        );

        log.info("Application {} sent to PCIC", submissionId);
    }


    public List<VerificationResponseDTO> getAllPendingVerifications() {
        List<VerificationRecord> pendingRecords = verificationRecordRepository.findByStatus(VerificationStatus.PENDING);
        List<ApplicationResponseDto> applications = fetchApplicationsForRecords(pendingRecords);

        // Merge ApplicationResponseDto and VerificationRecord fields into VerificationResponseDTO
        return pendingRecords.stream().map(record -> {
            ApplicationResponseDto app = applications.stream()
                    .filter(a -> a.getId().equals(record.getSubmissionId()))
                    .findFirst()
                    .orElse(null);

            return verificationRecordMapper.toVerificationResponseDTO(record, app);
        }).toList();
    }

    private List<ApplicationResponseDto> fetchApplicationsForRecords(List<VerificationRecord> pendingRecords) {
        return pendingRecords.stream()
                .map(record -> applicationClient.getApplicationById(record.getSubmissionId(), String.valueOf(record.getUploadedBy())))
                .toList();
    }

    public List<VerificationResponseDTO> getSubmissionsByBatchId(UUID batchId) {

        Batch batch  = batchRepository.findById(batchId)
                .orElseThrow(() -> ApiException.notFound("Batch not found: " + batchId));

        List<VerificationRecord> records = batch.getVerificationRecords();
        return records.stream()
                .filter(record -> record.getStatus() != VerificationStatus.COMPLETED && !record.isForwarded())
                .map(
                record -> {
                    ApplicationResponseDto app = applicationClient.getApplicationById(record.getSubmissionId(), String.valueOf(record.getUploadedBy()));
                    return verificationRecordMapper.toVerificationResponseDTO(record, app);
                }
        ).toList();
    }
}
