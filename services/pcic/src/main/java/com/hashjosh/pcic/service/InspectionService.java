package com.hashjosh.pcic.service;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.kafkacommon.application.ClaimProcessedEvent;
import com.hashjosh.kafkacommon.application.InspectionCompletedEvent;
import com.hashjosh.kafkacommon.application.InspectionScheduledEvent;
import com.hashjosh.kafkacommon.application.PolicyIssuedEvent;
import com.hashjosh.pcic.clients.ScheduleClient;
import com.hashjosh.pcic.entity.Claim;
import com.hashjosh.pcic.entity.InspectionRecord;
import com.hashjosh.pcic.entity.Policy;
import com.hashjosh.constant.pcic.enums.InspectionStatus;
import com.hashjosh.pcic.kafka.PcicProducer;
import com.hashjosh.pcic.repository.ClaimRepository;
import com.hashjosh.pcic.repository.InspectionRecordRepository;
import com.hashjosh.pcic.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InspectionService {

    private final InspectionRecordRepository inspectionRepository;
    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final PcicProducer producer;
    private final PolicyService policyService;
    private final ClaimService claimService;
    private final ScheduleClient scheduleClient;


    public ScheduleResponseDto scheduleInspection(UUID submissionId, ScheduleRequestDto dto) {
        InspectionRecord inspection = inspectionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalStateException("Inspection record not found: " + submissionId));
        if (inspection.getStatus() != InspectionStatus.PENDING) {
            throw new IllegalStateException("Inspection not pending: " + submissionId);
        }

        ScheduleResponseDto schedule = scheduleClient.createSchedule(dto);

        producer.publishEvent("application-lifecycle",
                new InspectionScheduledEvent(
                        submissionId,
                        inspection.getSubmittedBy(),
                        schedule.getId(),
                        schedule.getScheduleDate(),
                        LocalDateTime.now()
                )
                );
        return schedule;
    }

    public void completeInspection(UUID submissionId, InspectionStatus status, String comments) {
        InspectionRecord record = inspectionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalStateException("Inspection record not found: " + submissionId));
        record.setStatus(status);
        record.setComments(comments);
        inspectionRepository.save(record);

        producer.publishEvent("application-lifecycle",
                InspectionCompletedEvent.builder()
                        .submissionId(record.getSubmissionId())
                        .userId(record.getSubmittedBy())
                        .status(status.name())
                        .comments(comments)
                        .inspectedAt(LocalDateTime.now())
                        .build()
                );

        if (status == InspectionStatus.COMPLETED && validateFurther(submissionId, record)) {
            Policy policy = policyService.createPolicy(submissionId);
            policyRepository.save(policy);

            producer.publishEvent("application-lifecycle",
                    new PolicyIssuedEvent(
                            submissionId,
                            record.getSubmittedBy(),
                            policy.getId(),
                            policy.getPolicyNumber(),
                            policy.getCoverageAmount(),
                            LocalDateTime.now()
                    ));


            Claim claim = claimService.createClaim(submissionId, policy.getId());
            claimRepository.save(claim);

            producer.publishEvent("application-lifecycle",
                    new ClaimProcessedEvent(
                            submissionId,
                            record.getSubmissionId(),
                            claim.getId(),
                            claim.getPayoutStatus().name(),
                            claim.getClaimAmount(),
                            LocalDateTime.now()
                    ));

        }
        log.info("Inspection completed for application: {}, status: {}", submissionId, status);
    }

    private boolean validateFurther(UUID submissionId, InspectionRecord record) {
        return record.getStatus() == InspectionStatus.COMPLETED; // Add custom validation
    }
}
