package com.hashjosh.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.application.dto.submission.ApplicationSubmissionDto;
import com.hashjosh.application.dto.validation.ValidationError;
import com.hashjosh.application.dto.validation.ValidationErrors;
import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.application.kafka.ApplicationProducer;
import com.hashjosh.application.mapper.ApplicationMapper;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationField;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.application.model.Document;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import com.hashjosh.application.repository.DocumentRepository;
import com.hashjosh.application.validators.FieldValidatorFactory;
import com.hashjosh.application.validators.ValidatorStrategy;
import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final ApplicationProducer  applicationProducer;
    private final DocumentServiceClient documentServiceClient;
    private final DocumentRepository documentRepository;

    public Application processSubmission(
            ApplicationSubmissionDto submission,
            List<MultipartFile> files
    ) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();

            submission.setUseId(UUID.fromString(userDetails.getUserId()));

            // Upload files first and create Document entities
            List<Document> documents = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    var docResponse = documentServiceClient.uploadDocument(file, userDetails.getUserId());
                    Document document = Document.builder()
                            .documentId(docResponse.getDocumentId())
                            .fileName(docResponse.getFileName())
                            .fileType(docResponse.getFileType())
                            .coordinates(submission.getCoordinates())
                            .uploadedAt(docResponse.getUploadedAt())
                            .build();

                    documentRepository.save(document);
                    documents.add(document);
                }
            }
            submission.setDocuments(documents);

            // We get the application type through the batch
            ApplicationType applicationType = applicationTypeRepository.findById(submission.getApplicationTypeId())
                    .orElseThrow(() -> ApiException.badRequest("Invalid application type ID"));

            List<ApplicationField> fields = applicationType.getSections().stream()
                    .flatMap(section -> section.getFields().stream())
                    .collect(Collectors.toList());

            List<ValidationError> validationErrors = validateSubmission(submission, fields);

            if (!validationErrors.isEmpty()) {
                throw ApiException.badRequest("Validation failed: " + validationErrors);
            }
            Application application = applicationMapper.toEntity(submission, applicationType);
            Application savedApplication = applicationRepository.save(application);

            applicationProducer.publishEvent("application-lifecycle",
                    ApplicationSubmittedEvent.builder()
                            .submissionId(savedApplication.getId())
                            .provider(applicationType.getProvider().getName())
                            .userId(UUID.fromString(userDetails.getUserId()))
                            .submittedAt(LocalDateTime.now())
                            .build()
            );

            return application;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.internalError("An error occurred while processing your application: " + e.getMessage());
        }
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
                .orElseThrow(() -> ApiException.notFound("Application not found with id "+ applicationId));
        return applicationMapper.toApplicationResponseDto(application);
    }


    public List<ApplicationResponseDto> findAll() {
        return applicationRepository.findAll()
                .stream().map(applicationMapper::toApplicationResponseDto).collect(Collectors.toList());
    }

    private Application findApplicationById(UUID applicationId) {
        return  applicationRepository.findById(applicationId)
                .orElseThrow(() -> ApiException.notFound("Application not found with id "+ applicationId));
    }

    public List<ApplicationResponseDto> findAllProviderApplication(
            String provider
    ) {

       ApplicationType type = applicationTypeRepository.findByProvider_Name(provider)
               .orElseThrow(() -> ApiException.notFound("Application type not found for provider "+ provider));

       List<Application> application = applicationRepository.findAllByApplicationTypeId(type.getId());

        return application.stream().map(applicationMapper::toApplicationResponseDto).collect(Collectors.toList());
    }

    public void deleteApplication(UUID applicationId) {
        Application application = findApplicationById(applicationId);
        applicationRepository.delete(application);
    }

}
