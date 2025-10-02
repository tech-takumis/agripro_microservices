package com.hashjosh.verification.service;

import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationVerificationContract;
import com.hashjosh.verification.clients.ApplicationClient;
import com.hashjosh.verification.config.CustomUserDetails;
import com.hashjosh.verification.dto.ApplicationResponseDto;
import com.hashjosh.verification.dto.VerificationRequest;
import com.hashjosh.verification.dto.VerificationResponse;
import com.hashjosh.verification.exception.ApplicationNotFoundException;
import com.hashjosh.verification.kafka.VerificationProducer;
import com.hashjosh.verification.mapper.VerificationMapper;
import com.hashjosh.verification.model.VerificationResult;
import com.hashjosh.verification.repository.VerificationResultRepository;
import com.hashjosh.verification.utils.KafkaUtils;
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
public class VerificationService {

    private final VerificationResultRepository verificationResultRepository;
    private final VerificationMapper verificationMapper;
    private final VerificationProducer verificationProducer;
    private final ApplicationClient applicationClient;
    private final KafkaUtils kafkaUtils;

    public VerificationResponse verify(UUID applicationId,
                                       VerificationRequest dto,
                                       HttpServletRequest request) {

        // Retrieve the authenticated user from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Authentication information is missing or invalid");
        }
        String userToken = userDetails.getToken();

        Optional<VerificationResult> savedVerification = verificationResultRepository.findByApplicationId(applicationId);
        if (savedVerification.isEmpty()) {
            throw new ApplicationNotFoundException(
                    "Application id " + applicationId + " not found",
                    HttpStatus.NOT_FOUND.value(),
                    request.getRequestURI()
            );
        }

        VerificationResult updatedVerification = verificationMapper.updateVerification(dto, savedVerification.get());
        VerificationResult result = verificationResultRepository.save(updatedVerification);

        // Define statuses for which events should be published
        if (dto.status().equals(ApplicationStatus.APPROVED_BY_MA.name()) ||
            dto.status().equals(ApplicationStatus.REJECTED_BY_MA.name())) {

            ApplicationVerificationContract contract = ApplicationVerificationContract.builder()
                    .eventId(result.getEventId())
                    .eventType(getEventType(dto.status()))
                    .schemaVersion(1)
                    .uploadedBy(result.getUploadedBy())
                    .eventType(EventType.fromString(dto.status()))
                    .applicationId(result.getApplicationId())
                    .status(result.getStatus())
                    .version(result.getVersion())
                    .build();

            // Publish the event
            verificationProducer.submitVerifiedApplication(contract);
        }

        // Return verification response
        return verificationMapper.toVerificationResponse(updatedVerification);
    }

    private EventType getEventType(String status) {
        return null;
    }


    // Get verifications by status
    public List<VerificationResponse> findByStatus(ApplicationStatus status) {
        return verificationResultRepository.findByStatus(status)
                .stream()
                .map(verificationMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get verifications by multiple statuses
    public List<VerificationResponse> findByStatuses(List<ApplicationStatus> statuses)
    {
        List<ApplicationStatus> validStatuses = statuses.stream()
                .filter(VERIFICATION_ALLOWED_STATUSES::contains)
                .toList();

        return verificationResultRepository.findByStatusIn(validStatuses)
                .stream()
                .map(verificationMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get all verifications
    public List<VerificationResponse> findAllVerification() {
        return verificationResultRepository.findAll()
                .stream()
                .map(verificationMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get verifications by inspection type and statuses
    public List<VerificationResponse> findByInspectionTypeAndStatuses(String inspectionType, List<ApplicationStatus> statuses) {
        return verificationResultRepository.findByStatusInAndInspectionType(statuses, inspectionType)
                .stream()
                .map(verificationMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    // Get verifications by application ID
    public Optional<VerificationResponse> findByApplicationId(UUID applicationId) {
        return verificationResultRepository.findByApplicationId(applicationId)
                .map(verificationMapper::toVerificationResponse);
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