package com.hashjosh.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.application.dto.*;
import com.hashjosh.application.exceptions.ApplicationNotFoundException;
import com.hashjosh.application.kafka.ApplicationProducer;
import com.hashjosh.application.mapper.ApplicationMapper;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationField;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import com.hashjosh.application.validators.FieldValidatorFactory;
import com.hashjosh.application.validators.ValidatorStrategy;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FieldValidatorFactory fieldValidatorFactory;
    private final ApplicationTypeRepository applicationTypeRepository;
    private final ApplicationMapper applicationMapper;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    private final ApplicationProducer applicationProducer;
    private final DocumentServiceClient documentServiceClient;

    /**
     * Gets the current user's authentication token
     * @return The JWT token of the current user
     */
    private String getCurrentUserToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                return ((CustomUserDetails) authentication.getPrincipal()).getToken();
            }
            logger.warn("Could not extract JWT token from authentication context - CustomUserDetails not found");
            return "";
        } catch (Exception e) {
            logger.error("Error getting current user token", e);
            return "";
        }
    }

    /**
     * Validates that all document IDs in the list exist in the document service
     * @param documentIds List of document IDs to validate
     * @return List of validation errors, empty if all documents exist
     */
    private List<ValidationError> validateDocumentIds(List<UUID> documentIds) {
        List<ValidationError> errors = new ArrayList<>();
        String token = getCurrentUserToken(); // You'll need to implement this method to get the current user's token
        
        for (UUID documentId : documentIds) {
            try {
                // Check if document exists
                boolean exists = documentServiceClient.documentExists(token, documentId);
                if (!exists) {
                    errors.add(new ValidationError(
                            "documents",
                            "Document not found with ID: " + documentId
                    ));
                }
            } catch (Exception e) {
                log.error("Error validating document with ID: {}", documentId, e);
                errors.add(new ValidationError(
                        "documents",
                        "Error validating document with ID: " + documentId + " - " + e.getMessage()
                ));
            }
        }
        
        return errors;
    }


    public ApplicationSubmissionResponse processSubmission(
            ApplicationSubmissionDto submission,
            CustomUserDetails userDetails) {

        // 1. Validate application type exists
        ApplicationType applicationType = applicationTypeRepository.findById(submission.getApplicationTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application type not found: " + submission.getApplicationTypeId()));

        // 2. Get all fields for this application type
        List<ApplicationField> fields = applicationType.getSections().stream()
                .flatMap(section -> section.getFields().stream())
                .collect(Collectors.toList());

        // 3. Validate document IDs if present
        if (submission.getDocumentIds() != null && !submission.getDocumentIds().isEmpty()) {
            List<ValidationError> documentValidationErrors = validateDocumentIds(submission.getDocumentIds());
            if (!documentValidationErrors.isEmpty()) {
                return ApplicationSubmissionResponse.builder()
                        .success(false)
                        .message("Document validation failed")
                        .errors(documentValidationErrors)
                        .build();
            }
        }

        // 4. Validate fields
        List<ValidationError> validationErrors = validateSubmission(submission, fields);

        if (!validationErrors.isEmpty()) {
            return ApplicationSubmissionResponse.builder()
                    .success(false)
                    .message("Validation failed")
                    .errors(validationErrors)
                    .build();
        }


        // 4. Process and save the application
        Application application = applicationMapper.toEntity(submission,applicationType, userDetails.getUserId());
        Application savedApplication = applicationRepository.save(application);

        publishApplicationStatus(savedApplication, userDetails);

        return ApplicationSubmissionResponse.builder()
                .success(true)
                .message("Application submitted successfully")
                .applicationId(savedApplication.getId())
                .build();
    }

    private List<ValidationError> validateSubmission(
            ApplicationSubmissionDto submission,
            List<ApplicationField> fields) {

        List<ValidationError> errors = new ArrayList<>();

        // 1. Check required fields
        fields.stream()
                .filter(ApplicationField::getRequired)
                .filter(field -> !submission.getFieldValues().containsKey(field.getKey()))
                .forEach(field -> errors.add(new ValidationError(
                        field.getKey(),
                        String.format("Field '%s' is required", field.getFieldName())
                )));

        // 2. Validate field types and constraints
        submission.getFieldValues().forEach((key, value) -> {
            fields.stream()
                    .filter(field -> field.getKey().equals(key))
                    .findFirst()
                    .ifPresent(field -> {
                        try {
                            // Get the appropriate validator for the field type
                            ValidatorStrategy validator = fieldValidatorFactory.getStrategy(field.getFieldType().name());
                            // Convert value to JsonNode and validate the field value
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode valueNode = objectMapper.valueToTree(value);
                            List<ValidationErrors> fieldErrors = validator.validate(field, valueNode);
                            if (fieldErrors != null && !fieldErrors.isEmpty()) {
                                // Since ValidationErrors is being used as a single error, we'll treat it as such
                                // and create a ValidationError with the same field and message
                                fieldErrors.forEach(validationError -> 
                                    errors.add(new ValidationError(
                                        field.getKey(),  // Using the field key as the field name
                                        validationError.toString()  // Convert the error to string as the message
                                    ))
                                );
                            }
                        } catch (IllegalArgumentException e) {
                            errors.add(new ValidationError(
                                field.getKey(),
                                String.format("Unsupported field type: %s", field.getFieldType())
                            ));
                        }
                    });
        });

        return errors;
    }


    public ApplicationResponseDto getApplicationById(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found!",
                        HttpStatus.NOT_FOUND.value()
                ));
        return applicationMapper.toApplicationResponseDto(application);
    }


    public List<ApplicationResponseDto> findAll() {
        return applicationRepository.findAll()
                .stream().map(applicationMapper::toApplicationResponseDto).collect(Collectors.toList());
    }


    public void publishApplicationStatus(Application application, CustomUserDetails user) {

        applicationProducer.publishEvent("application-lifecycle",
               ApplicationSubmittedEvent.builder()
                       .submissionId(application.getId())
                       .userId(UUID.fromString(user.getUserId()))
                       .applicationTypeId(application.getApplicationType().getId())
                       .dynamicFields(application.getDynamicFields())
                       .documentIds(application.getDocumentId())
                       .submittedAt(LocalDateTime.now())
                       .build()
        );
    }


    public List<ApplicationResponseDto> findApplicationbyType(UUID applicationTypeId) {
        return applicationRepository.findByApplicationTypeId(applicationTypeId)
                .stream().map(applicationMapper::toApplicationResponseDto)
                .collect(Collectors.toList());
    }

    private Application findApplicationById(UUID applicationId) {
        return  applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found with id "+ applicationId,
                        HttpStatus.BAD_REQUEST.value()
                ));
    }
}
