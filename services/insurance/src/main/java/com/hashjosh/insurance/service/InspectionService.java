package com.hashjosh.insurance.service;

import com.hashjosh.constant.pcic.enums.PolicyStatus;
import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.insurance.dto.inspection.InspectionRequestDto;
import com.hashjosh.insurance.dto.policy.PolicyRequest;
import com.hashjosh.insurance.entity.InspectionRecord;
import com.hashjosh.insurance.entity.Insurance;
import com.hashjosh.insurance.entity.Policy;
import com.hashjosh.insurance.exception.ApiException;
import com.hashjosh.insurance.kafka.KafkaProducer;
import com.hashjosh.insurance.repository.InspectionRecordRepository;
import com.hashjosh.insurance.repository.InsuranceRepository;
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
    private final InsuranceRepository insuranceRepository;
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
                        inspection.getInsurance().getSubmittedBy(),
                        schedule.getId(),
                        schedule.getScheduleDate(),
                        LocalDateTime.now()
                )
                );
        return schedule;
    }

    public void completeInspection(UUID insuranceId, InspectionRequestDto requestDto, PolicyRequest request) {
        Insurance insurance = getInsurance(insuranceId);
        InspectionRecord record = insurance.getInspectionRecord();
        record.setStatus(requestDto.getStatus());
        record.setComments(requestDto.getComments());
        inspectionRepository.save(record);

        producer.publishEvent("application-lifecycle",
                InspectionCompletedEvent.builder()
                        .submissionId(record.getInsurance().getSubmissionId())
                        .userId(record.getInsurance().getSubmittedBy())
                        .status(requestDto.getStatus().name())
                        .comments(requestDto.getComments() != null ? requestDto.getComments() : "")
                        .inspectedAt(LocalDateTime.now())
                        .build()
                );

        if (requestDto.getStatus() == InspectionStatus.COMPLETED && validateFurther(insuranceId, record)) {
            Policy policy = policyService.createPolicy(request);

            producer.publishEvent("application-lifecycle",
                    new PolicyIssuedEvent(
                            insuranceId,
                            record.getInsurance().getSubmittedBy(),
                            policy.getId(),
                            policy.getPolicyNumber(),
                            policy.getCoverageAmount(),
                            LocalDateTime.now()
                    ));

        }
        log.info("Inspection completed for application: {}, status: {}", insuranceId, requestDto.getStatus());
    }

    private boolean validateFurther(UUID submissionId, InspectionRecord record) {
        return record.getStatus() == InspectionStatus.COMPLETED; // Add custom validation
    }

    private Insurance getInsurance(UUID insuranceId) {
        return  insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> ApiException.notFound("Insurance not found"));
    }

    private String getPolicyNumber() {
        return "";
    }
}
