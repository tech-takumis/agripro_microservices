package com.hashjosh.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.application.dto.ApplicationResponseDto;
import com.hashjosh.application.dto.ApplicationSubmissionDto;
import com.hashjosh.application.dto.DocumentResponse;
import com.hashjosh.application.dto.ValidationErrors;
import com.hashjosh.application.enums.ApplicationStatus;
import com.hashjosh.application.exceptions.ApplicationNotFoundException;
import com.hashjosh.application.exceptions.InvalidStatusException;
import com.hashjosh.application.kafka.ApplicationProducer;
import com.hashjosh.application.mapper.ApplicationMapper;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationField;
import com.hashjosh.application.model.ApplicationSection;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.application.validators.FieldValidatorFactory;
import com.hashjosh.application.validators.FileValidator;
import com.hashjosh.application.validators.ValidatorStrategy;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FieldValidatorFactory fieldValidatorFactory;
    private final ApplicationTypeService applicationTypeService;
    private final ApplicationMapper applicationMapper;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    private final ApplicationProducer applicationProducer;
    private final DocumentServiceClient documentServiceClient;

    @Transactional
    public List<ValidationErrors> submitApplication(ApplicationSubmissionDto submission,
                                                    UUID applicationTypeId, HttpServletRequest request
                                                    ) throws FileUploadException {
        // Validate input
        if (submission == null || submission.fieldValues() == null) {
            return List.of(new ValidationErrors("fieldValues", "Field values cannot be null"));
        }

        JsonNode fieldValues = submission.fieldValues();
        if (!fieldValues.isObject()) {
            return List.of(new ValidationErrors("fieldValues", "Field values must be a JSON object"));
        }

        // Convert JsonNode to ObjectNode
        ObjectNode mutableFields;
        try {
            mutableFields = (ObjectNode) fieldValues; // Direct cast since fieldValues is already a JsonNode
        } catch (ClassCastException e) {
            return List.of(new ValidationErrors("fieldValues", "Field values must be a valid JSON object"));
        }

        ApplicationType applicationType = applicationTypeService.getApplicationTypeById(applicationTypeId);

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = customUserDetails.getUserId();

        List<ValidationErrors> allErrors = validateAndProcessFields(applicationType,
                mutableFields, submission,applicationTypeId, userId ,request);

        if (!allErrors.isEmpty()) {
            return allErrors;
        }

        // No need to convert back to JsonNode since mutableFields is already a JsonNode
       Application application = applicationRepository.saveAndFlush(applicationMapper.toApplication(applicationType, mutableFields, userId));
        logger.info("Application submitted successfully::: {}", application.getId());
       ApplicationDto applicationDto = applicationMapper.toApplicationDto(application);


        // Send event application submit to application event topic
        applicationProducer.submitApplication(
                new ApplicationContract(
                        UUID.randomUUID(),
                        "application-submitted",
                        1,
                        application.getId(),
                        LocalDateTime.now(),
                        applicationDto
                )
        );

        return List.of();
    }




    public List<ValidationErrors> validateAndProcessFields(ApplicationType applicationType,
                                                           ObjectNode mutableFields,
                                                           ApplicationSubmissionDto submission,
                                                           UUID applicationId,
                                                           String userId,
                                                           HttpServletRequest request

    ) throws FileUploadException {

        List<ValidationErrors> allErrors = new ArrayList<>();
        for (ApplicationSection section: applicationType.getSections()) {
            for(ApplicationField field: section.getFields()) {
                String key = field.getKey();
                String fieldName = field.getFieldName();
                String fieldType = field.getFieldType().name();
                JsonNode submittedValue = mutableFields.get(key);

                if(field.getRequired() && submittedValue == null) {
                    allErrors.add(new ValidationErrors(key,"Field " + fieldName + " is required"));
                    continue;
                }
                if(submittedValue != null && !submittedValue.isMissingNode()) {
                    ValidatorStrategy validator =  fieldValidatorFactory.getStrategy(fieldType);

                    List<ValidationErrors> fieldErrors = validator.validate(field, submittedValue);
                    allErrors.addAll(fieldErrors);

                    if (fieldErrors.isEmpty() && validator instanceof FileValidator fileValidator) {
                        String savePath = fileValidator.saveFile(submittedValue,
                                submission.files(), applicationId, userId, request);
                        mutableFields.put(key, savePath);
                    }

                }

            }
        }
        return allErrors;
    }


    public ApplicationResponseDto getApplicationById(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found!",
                        HttpStatus.NOT_FOUND.value(),
                        "/api/v1/applications/" + applicationId
                ));
        return applicationMapper.toApplicationResponseDto(application);
    }



    public ApplicationResponseDto verifiedApplicationStatus(UUID applicationId, String status) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found!",
                        HttpStatus.NOT_FOUND.value(),
                        "/api/v1/applications/" + applicationId
                ));

        ApplicationStatus newStatus;
        try{
            newStatus = ApplicationStatus.valueOf(status);
        }catch (IllegalArgumentException e){
            throw new InvalidStatusException(
                    "Invalid status: " + status + ". Allowed values: VERIFIED, REJECTED",
                    HttpStatus.FORBIDDEN.value(),
                    "/api/v1/applications/"+applicationId+"/status"
            );
        }
        application.setStatus(newStatus);

        ApplicationDto applicationDto = applicationMapper.toApplicationDto(application);

        applicationProducer.submitApplication(
                new ApplicationContract(
                        UUID.randomUUID(),
                       "application-verified",
                        1,
                        application.getId(),
                        LocalDateTime.now(),
                        applicationDto
                )
        );
        return applicationMapper.toApplicationResponseDto(application);
    }

    public List<ApplicationResponseDto> findAll() {
        return applicationRepository.findAll()
                .stream().map(applicationMapper::toApplicationResponseDto).collect(Collectors.toList());
    }

    public String testFileUpload(MultipartFile file,HttpServletRequest request) throws IOException {
        String token = request.getHeader("Authorization").substring(7);
        UUID applicationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        DocumentResponse document = documentServiceClient.uploadDocument(token,applicationId,userId,file);
        return "File uploaded successfully";
    }
}
