package com.hashjosh.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.application.dto.*;
import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.application.kafka.ApplicationProducer;
import com.hashjosh.application.mapper.ApplicationMapper;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationField;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.application.model.Document;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import com.hashjosh.application.validators.FieldValidatorFactory;
import com.hashjosh.application.validators.ValidatorStrategy;
import com.hashjosh.constant.document.dto.DocumentResponse;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FieldValidatorFactory fieldValidatorFactory;
    private final ApplicationTypeRepository applicationTypeRepository;
    private final ApplicationMapper applicationMapper;
    private final ApplicationProducer applicationProducer;
    private final DocumentServiceClient documentServiceClient;

    public ApplicationSubmissionResponse processSubmission(
            ApplicationSubmissionDto submission,
            List<MultipartFile> files
    ) {
        CustomUserDetails userDetails = getCurrentUser();
        submission.setUploadedBy(UUID.fromString(userDetails.getUserId()));

        try {
            ApplicationType applicationType = validateAndGetApplicationType(submission);
            List<ApplicationField> fields = extractApplicationFields(applicationType);

            // Validate submission fields
            List<ValidationError> validationErrors = validateSubmission(submission, fields);
            if (!validationErrors.isEmpty()) {
                // Throw runtime exception if validation errors exist
                throw new ApiException("Validation failed", validationErrors, HttpStatus.BAD_REQUEST);
            }

            // Process documents
            Set<Document> documents = processDocuments(files, userDetails);

            // Save application
            Application savedApplication = saveApplication(submission, applicationType, documents);

            applicationProducer.publishEvent("application-lifecycle",
                    ApplicationSubmittedEvent.builder()
                            .submissionId(savedApplication.getId())
                            .recipientType(savedApplication.getApplicationType().getRecipientType())
                            .uploadedBY(UUID.fromString(userDetails.getUserId()))
                            .applicationTypeId(savedApplication.getApplicationType().getId())
                            .dynamicFields(savedApplication.getDynamicFields())
                            .documentIds(savedApplication.getDocumentsUploaded().stream().map(Document::getId).toList())
                            .submittedAt(LocalDateTime.now())
                            .build()
            );

            // Return success response with applicationId only (other fields will be set in controller)
            return ApplicationSubmissionResponse.builder()
                    .success(true)
                    .message("Application submitted successfully")
                    .applicationId(savedApplication.getId())
                    .build();

        } catch (Exception e) {
            log.error("Error processing application submission", e);
            throw ApiException.internalError("Failed to process application submission");
        }
    }

    private CustomUserDetails getCurrentUser() {
        return (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private ApplicationType validateAndGetApplicationType(ApplicationSubmissionDto submission) {
        return applicationTypeRepository.findById(submission.getApplicationTypeId())
                .orElseThrow(() -> ApiException.notFound("This type of application does not exist"));
    }

    private List<ApplicationField> extractApplicationFields(ApplicationType applicationType) {
        return applicationType.getSections().stream()
                .flatMap(section -> section.getFields().stream())
                .collect(Collectors.toList());
    }

    private Set<Document> processDocuments(List<MultipartFile> files, CustomUserDetails userDetails) {
        return files.stream()
                .map(file -> processDocument(file, userDetails))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Document processDocument(MultipartFile file, CustomUserDetails userDetails) {
        try {
            DocumentResponse response = documentServiceClient.uploadDocument(file, userDetails.getUserId());
            return applicationMapper.toDocumentEntity(response);
        } catch (Exception e) {
            log.error("Failed to process document: {}", file.getOriginalFilename(), e);
            return null;
        }
    }

    private Application saveApplication(
            ApplicationSubmissionDto submission,
            ApplicationType applicationType,
            Set<Document> documents
    ) {
        Application application = applicationMapper.toEntity(submission, applicationType, documents);
        return applicationRepository.save(application);
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

        if (!errors.isEmpty()) {
            // Throw runtime exception if validation errors exist
            throw new RuntimeException("Validation failed: " + errors);
        }

        return errors;
    }


    public ApplicationResponseDto getApplicationById(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> ApiException.notFound("Application not found"));
        return applicationMapper.toApplicationResponseDto(application);
    }


    public List<ApplicationResponseDto> findAll() {
        return applicationRepository.findAll()
                .stream().map(applicationMapper::toApplicationResponseDto).collect(Collectors.toList());
    }


    public List<ApplicationResponseDto> findApplicationByType(UUID applicationTypeId) {
        return applicationRepository.findByApplicationTypeId(applicationTypeId)
                .stream().map(applicationMapper::toApplicationResponseDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDto> findAllAgricultureApplication() {
        ApplicationType type = applicationTypeRepository.findByNameContains("Crop Insurance Application")
                .orElseThrow(() -> ApiException.notFound("Agriculture application type not found"));
        List<Application> application = applicationRepository.findByApplicationTypeId(type.getId());

        return application.stream().map(applicationMapper::toApplicationResponseDto).collect(Collectors.toList());
    }
}
