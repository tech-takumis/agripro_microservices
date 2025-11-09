package com.hashjosh.insurance.service;

import com.hashjosh.constant.pcic.enums.InspectionStatus;
import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import com.hashjosh.insurance.clients.ScheduleClient;
import com.hashjosh.insurance.dto.inspection.InspectionRequestDto;
import com.hashjosh.insurance.dto.inspection.InspectionResponse;
import com.hashjosh.insurance.entity.InspectionRecord;
import com.hashjosh.insurance.entity.Insurance;
import com.hashjosh.insurance.entity.Policy;
import com.hashjosh.insurance.exception.ApiException;
import com.hashjosh.insurance.kafka.KafkaProducer;
import com.hashjosh.insurance.mapper.InspectionMapper;
import com.hashjosh.insurance.repository.InspectionRecordRepository;
import com.hashjosh.insurance.repository.InsuranceRepository;
import com.hashjosh.insurance.repository.PolicyRepository;
import com.hashjosh.kafkacommon.application.InspectionCompletedEvent;
import com.hashjosh.kafkacommon.application.InspectionScheduledEvent;
import com.hashjosh.kafkacommon.application.PolicyIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    private final InspectionMapper inspectionMapper;

    public ScheduleResponseDto scheduleInspection(UUID insuranceId, ScheduleRequestDto dto) {
        InspectionRecord inspection = inspectionRepository.findByInsurance_Id(insuranceId)
                .orElseThrow(() -> new IllegalStateException("Inspection record not found: " + insuranceId));
        if (inspection.getStatus() != InspectionStatus.PENDING) {
            throw new IllegalStateException("Inspection not pending: " + insuranceId);
        }

        ScheduleResponseDto schedule = scheduleClient.createSchedule(dto);

        producer.publishEvent("application-inspection-schedule",
                new InspectionScheduledEvent(
                        insuranceId,
                        inspection.getInsurance().getSubmittedBy(),
                        schedule.getId(),
                        schedule.getScheduleDate(),
                        LocalDateTime.now()
                )
                );
        return schedule;
    }

    public void completeInspection(UUID insuranceId, InspectionRequestDto requestDto) {
        Insurance insurance = getInsurance(insuranceId);
        InspectionRecord record = insurance.getInspectionRecord();
        record.setStatus(requestDto.getStatus());
        record.setComments(requestDto.getComments());
        inspectionRepository.save(record);

        producer.publishEvent("application-inspection-completed",
                InspectionCompletedEvent.builder()
                        .submissionId(record.getInsurance().getSubmissionId())
                        .userId(record.getInsurance().getSubmittedBy())
                        .status(requestDto.getStatus().name())
                        .comments(requestDto.getComments() != null ? requestDto.getComments() : "")
                        .inspectedAt(LocalDateTime.now())
                        .build()
                );

        if (requestDto.getStatus() == InspectionStatus.COMPLETED && validateFurther(insuranceId, record)) {
            Policy policy = policyService.createPolicy(requestDto.getPolicy());

            producer.publishEvent("application-policy-issued",
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

    public List<InspectionResponse> getAllInspections() {
        return inspectionRepository.findAll().stream().map(inspectionMapper::toInspectionResposeDTO).toList();
    }

    public void deleteInspection(UUID inspectionId) {
        inspectionRepository.deleteById(inspectionId);
    }

    private Insurance getInsurance(UUID insuranceId) {
        return  insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> ApiException.notFound("Insurance not found"));
    }

    private boolean validateFurther(UUID submissionId, InspectionRecord record) {
        return record.getStatus() == InspectionStatus.COMPLETED; // Add custom validation
    }


}
