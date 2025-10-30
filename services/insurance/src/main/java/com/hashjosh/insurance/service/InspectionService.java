package com.hashjosh.insurance.service;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.insurance.dto.inspection.InspectionRequestDto;
import com.hashjosh.insurance.entity.InspectionRecord;
import com.hashjosh.insurance.entity.Policy;
import com.hashjosh.insurance.exception.ApiException;
import com.hashjosh.insurance.kafka.KafkaProducer;
import com.hashjosh.insurance.repository.InspectionRecordRepository;
import com.hashjosh.insurance.repository.PolicyRepository;
import com.hashjosh.kafkacommon.application.InspectionCompletedEvent;
import com.hashjosh.kafkacommon.application.InspectionScheduledEvent;
import com.hashjosh.kafkacommon.application.PolicyIssuedEvent;
import com.hashjosh.insurance.clients.ScheduleClient;
import com.hashjosh.constant.pcic.enums.InspectionStatus;
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
    private final PolicyRepository policyRepository;
    private final KafkaProducer producer;
    private final PolicyService policyService;
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

    public void completeInspection(UUID submissionId, InspectionRequestDto requestDto) {
        InspectionRecord record = inspectionRepository.findBySubmissionId(submissionId)
                .orElseThrow(() -> ApiException.notFound("Inspection record not found for submission: " + submissionId));

        record.setStatus(requestDto.getStatus());
        record.setComments(requestDto.getComments());
        inspectionRepository.save(record);

        producer.publishEvent("application-lifecycle",
                InspectionCompletedEvent.builder()
                        .submissionId(record.getSubmissionId())
                        .userId(record.getSubmittedBy())
                        .status(requestDto.getStatus().name())
                        .comments(requestDto.getComments() != null ? requestDto.getComments() : "")
                        .inspectedAt(LocalDateTime.now())
                        .build()
                );

        if (requestDto.getStatus() == InspectionStatus.COMPLETED && validateFurther(submissionId, record)) {
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

        }
        log.info("Inspection completed for application: {}, status: {}", submissionId, requestDto.getStatus());
    }

    private boolean validateFurther(UUID submissionId, InspectionRecord record) {
        return record.getStatus() == InspectionStatus.COMPLETED; // Add custom validation
    }
}
