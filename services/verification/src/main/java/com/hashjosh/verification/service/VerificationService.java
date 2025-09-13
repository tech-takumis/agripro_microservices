package com.hashjosh.verification.service;

import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationDto;
import com.hashjosh.kafkacommon.verification.VerificationContract;
import com.hashjosh.kafkacommon.verification.VerificationDto;
import com.hashjosh.verification.clients.ApplicationClient;
import com.hashjosh.verification.config.CustomUserDetails;
import com.hashjosh.verification.dto.ApplicationResponseDto;
import com.hashjosh.verification.dto.VerificationRequest;
import com.hashjosh.verification.dto.VerificationResponse;
import com.hashjosh.verification.enums.VerificationStatus;
import com.hashjosh.verification.exception.ApplicationNotFoundException;
import com.hashjosh.verification.exception.TokenNotFoundException;
import com.hashjosh.verification.kafka.VerificationProducer;
import com.hashjosh.verification.mapper.VerificationMapper;
import com.hashjosh.verification.model.VerificationResult;
import com.hashjosh.verification.repository.VerificationResultRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationResultRepository verificationResultRepository;
    private final VerificationMapper verificationMapper;
    private final VerificationProducer verificationProducer;
    private final ApplicationClient applicationClient;

    public VerificationResponse verify(UUID applicationId,
                                       VerificationRequest dto,
                                       HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if(token == null || !token.startsWith("Bearer ")){
            throw new TokenNotFoundException(
                    "Token not found when tried to verify the application",
                    HttpStatus.BAD_REQUEST.value(),
                    request.getRequestURI()
            );
        }

        String finalToken = token.substring(7);


        Optional<VerificationResult> savedVerification = verificationResultRepository.findByApplicationId(applicationId);
        if (savedVerification.isEmpty()) {
            throw new ApplicationNotFoundException(
                    "Application id " + applicationId + " not found",
                    HttpStatus.NOT_FOUND.value(),
                    request.getRequestURI()
            );
        }

        VerificationResult updatedVerification = verificationMapper.updateVerification(dto,savedVerification.get());

        VerificationResult result = verificationResultRepository.save(updatedVerification);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        boolean isApproved = dto.status().equals(VerificationStatus.APPROVED.name());
        boolean isRejected = dto.status().equals(VerificationStatus.REJECTED.name());

        if(isApproved || isRejected){

            ApplicationResponseDto application = applicationClient.getApplicationById(finalToken,result.getApplicationId(),request);


            ApplicationDto applicationDto = new ApplicationDto(
                result.getId(),
                    application.applicationTypeId(),
                    user.getUserId(),
                    result.getStatus().name(),
                    result.getVersion()
            );

            ApplicationContract contract = new ApplicationContract(
                    result.getEventId(),
                    isApproved ? "application-verified" :"application-rejected",
                    1,
                    result.getApplicationId(),
                    LocalDateTime.now(),
                    applicationDto
            );

            verificationProducer.submitVerifiedApplication(contract);
        }

        return verificationMapper.toVerificationResponse(updatedVerification);
    }

    public List<VerificationResponse> findAllVerifiedByAdjusterApplication(boolean isApproved) {
        return verificationResultRepository.findByApprovedByAdjuster(isApproved)
                .stream()
                .map(verificationMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    public List<VerificationResponse> findAllApprovedByUnderwriter(boolean isVerified) {
        return verificationResultRepository.findByVerifiedByUnderwriter(isVerified)
                .stream()
                .map(verificationMapper::toVerificationResponse)
                .collect(Collectors.toList());
    }

    public List<VerificationResponse> findAllVerification() {
            return verificationResultRepository.findAll()
                    .stream()
                    .map(verificationMapper::toVerificationResponse)
                    .collect(Collectors.toList());
    }

    public List<VerificationResponse> findAllVerifiedByAdjusterAndUnderwriter(Boolean isVerifiedByAdjuster, Boolean isApprovedByUnderwriter) {
        return verificationResultRepository.findAllByApprovedByAdjusterAndVerifiedByUnderwriter(isVerifiedByAdjuster,isApprovedByUnderwriter)
                .stream()
                .map(verificationMapper::toVerificationResponse)
                .collect(Collectors.toList());

    }
}
