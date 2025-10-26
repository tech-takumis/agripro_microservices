package com.hashjosh.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.clients.AgricultureHttpClient;
import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.constant.application.ApplicationResponseDto;
import com.hashjosh.application.dto.submission.ApplicationSubmissionDto;
import com.hashjosh.application.dto.validation.ValidationError;
import com.hashjosh.application.dto.validation.ValidationErrors;
import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.application.mapper.ApplicationMapper;
import com.hashjosh.application.model.*;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import com.hashjosh.application.repository.BatchRepository;
import com.hashjosh.application.validators.FieldValidatorFactory;
import com.hashjosh.application.validators.ValidatorStrategy;
import com.hashjosh.constant.verification.VerificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    private final AgricultureHttpClient agricultureHttpClient;
    private final BatchRepository batchRepository;


    public Application processSubmission(
            ApplicationSubmissionDto submission) {

        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();

            // Set the userId
            submission.setUseId(UUID.fromString(userDetails.getUserId()));

            // Fetch all batches for the application type, ordered by oldest first
            List<Batch> batches = batchRepository.findAllByApplicationTypeIdOrderByCreatedAtAsc(submission.getApplicationTypeId());
            Batch selectedBatch = null;
            for (Batch batch : batches) {
                // Check if batch is available (not full and not closed)
                boolean notFull = batch.getApplications().size() < batch.getMaxApplications();
                boolean available = batch.isAvailable(); // Assuming isAvailable() checks for open status
                if (notFull && available) {
                    selectedBatch = batch;
                    break;
                }
            }
            if (selectedBatch == null) {
                throw ApiException.badRequest("No available batch for this application type");
            }

            // We get the application type through the batch
            ApplicationType applicationType = selectedBatch.getApplicationType();
            List<ApplicationField> fields = applicationType.getSections().stream()
                    .flatMap(section -> section.getFields().stream())
                    .collect(Collectors.toList());


            // 4. Validate fields
            List<ValidationError> validationErrors = validateSubmission(submission, fields);

            submission.setDocumentIds(submission.getDocumentIds());

            if (!validationErrors.isEmpty()) {
                throw ApiException.badRequest("Validation failed: " + validationErrors);
            }

            Application application = applicationMapper.toEntity(submission, selectedBatch);
            Application savedApplication = applicationRepository.save(application);

            // Increment the batch's application count and save
            selectedBatch.setMaxApplications(selectedBatch.getMaxApplications() + 1);
            batchRepository.save(selectedBatch);

            agricultureHttpClient.submitApplication(
                    VerificationRequestDto.builder()
                            .submissionId(savedApplication.getId())
                            .uploadedBy(UUID.fromString(userDetails.getUserId()))
                            .report("Application submitted for verification")
                            .build(),
                    userDetails.getUserId()
            );

            return application;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.internalError("An error occurred while processing your application: " + e.getMessage());
        }
    }

    public List<ApplicationResponseDto> findAllApplicationByBatchName(String bacthName) {

        Batch batch = batchRepository.findByName(bacthName)
                .orElseThrow(() -> ApiException.notFound("Batch not found with id "+ bacthName));
        ApplicationType type = batch.getApplicationType();
        List<Application> applications = applicationRepository.findAllByBatchNameAndApplicationTypeId(
                bacthName,
                type.getId()
        );

        return applications.stream()
                .map(applicationMapper::toApplicationResponseDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDto> findAllApplicationByBatchId(UUID batchId) {

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> ApiException.notFound("Batch not found with id "+ batchId));
        ApplicationType type = batch.getApplicationType();
        List<Application> applications = applicationRepository.findAllByBatchIdAndApplicationTypeId(
                batchId,
                type.getId()
        );

        return applications.stream()
                .map(applicationMapper::toApplicationResponseDto)
                .collect(Collectors.toList());
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
