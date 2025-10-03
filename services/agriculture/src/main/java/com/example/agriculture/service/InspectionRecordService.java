package com.example.agriculture.service;

import com.example.agriculture.clients.ApplicationClient;
import com.example.agriculture.config.CustomUserDetails;
import com.example.agriculture.dto.*;
import com.example.agriculture.entity.InspectionRecord;
import com.example.agriculture.exception.ApplicationNotFoundException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.mapper.InspectionRecordMapper;
import com.example.agriculture.repository.InspectionRecordResultRepository;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.InspectionCompletedEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectionRecordService {

    private final InspectionRecordResultRepository inspectionRecordResultRepository;
    private final InspectionRecordMapper inspectionRecordMapper;
    private final AgricultureProducer agricultureProducer;
    private final ApplicationClient applicationClient;

    public InspectionRecordResponse verify(UUID applicationId,
                                           InspectionRecordRequest dto,
                                           HttpServletRequest request) {

        // Retrieve the authenticated user from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Authentication information is missing or invalid");
        }

        Optional<InspectionRecord> savedVerification = inspectionRecordResultRepository.findByApplicationId(applicationId);
        if (savedVerification.isEmpty()) {
            throw new ApplicationNotFoundException(
                    "Application id " + applicationId + " not found",
                    HttpStatus.NOT_FOUND.value()
            );
        }

        InspectionRecord updatedVerification = inspectionRecordMapper.updateVerification(dto, savedVerification.get());
        InspectionRecord result = inspectionRecordResultRepository.save(updatedVerification);

        InspectionCompletedEvent event = InspectionCompletedEvent.builder()
                .submissionId(result.getSubmissionId())
                .uploadedBy(result.getUploadedBy())
                .userId(userDetails.getAgriculture().getId())
                .inspectionType(result.getInspectionType())
                .report(result.getReport())
                .createdAt(LocalDateTime.now())
                .build();

        // Publish the event
        agricultureProducer.publishEvent("agriculture-events", event);

        // Return verification response
        return inspectionRecordMapper.toVerificationResponse(updatedVerification);
    }

    private EventType getEventType(String status) {
        return null;
    }


    // Get verifications by status
    public List<InspectionRecordResponse> findByStatus(ApplicationStatus status) {
        return inspectionRecordResultRepository.findByStatus(status)
                .stream()
                .map(inspectionRecordMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get verifications by multiple statuses
    public List<InspectionRecordResponse> findByStatuses(List<ApplicationStatus> statuses)
    {
        List<ApplicationStatus> validStatuses = statuses.stream()
                .filter(VERIFICATION_ALLOWED_STATUSES::contains)
                .toList();

        return inspectionRecordResultRepository.findByStatusIn(validStatuses)
                .stream()
                .map(inspectionRecordMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get all verifications
    public List<InspectionRecordResponse> findAllVerification() {
        return inspectionRecordResultRepository.findAll()
                .stream()
                .map(inspectionRecordMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get verifications by inspection type and statuses
    public List<InspectionRecordResponse> findByInspectionTypeAndStatuses(String inspectionType, List<ApplicationStatus> statuses) {
        return inspectionRecordResultRepository.findByStatusInAndInspectionType(statuses, inspectionType)
                .stream()
                .map(inspectionRecordMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get verifications by application ID
    public Optional<InspectionRecordResponse> findByApplicationId(UUID applicationId) {
        return inspectionRecordResultRepository.findByApplicationId(applicationId)
                .map(inspectionRecordMapper::toVerificationResponse);
    }

    private static final Set<ApplicationStatus> VERIFICATION_ALLOWED_STATUSES = Set.of(
            ApplicationStatus.APPROVED_BY_MA,
            ApplicationStatus.REJECTED_BY_MA,
            ApplicationStatus.UNDER_REVIEW_BY_MA,
            ApplicationStatus.UNDER_REVIEW_BY_AEW,
            ApplicationStatus.APPROVED_BY_AEW,
            ApplicationStatus.REJECTED_BY_AEW
    );
}