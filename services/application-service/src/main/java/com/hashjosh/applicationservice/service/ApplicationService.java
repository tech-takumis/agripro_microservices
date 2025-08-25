package com.hashjosh.applicationservice.service;

import com.hashjosh.applicationservice.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hashjosh.applicationservice.dto.ApplicationSubmissionDto;
import com.hashjosh.applicationservice.configs.CustomUserDetails;
import com.hashjosh.applicationservice.exceptions.ApplicationNotFoundException;
import com.hashjosh.applicationservice.mapper.ApplicationMapper;
import com.hashjosh.applicationservice.model.ApplicationFields;
import com.hashjosh.applicationservice.model.ApplicationSection;
import com.hashjosh.applicationservice.model.ApplicationType;
import com.hashjosh.applicationservice.repository.ApplicationRepository;
import com.hashjosh.applicationservice.repository.ApplicationTypeRepository;
import com.hashjosh.applicationservice.validators.FieldValidatorFactory;
import com.hashjosh.applicationservice.validators.FileValidator;
import com.hashjosh.applicationservice.validators.ValidatorStrategy;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationTypeRepository applicationTypeRepository;
    private final ObjectMapper objectMapper;
    private final FieldValidatorFactory fieldValidatorFactory;
    private final ApplicationMapper applicationMapper;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Transactional
    public List<ValidationErrors> submitApplication(ApplicationSubmissionDto submission,
                                                    Long applicationId,
                                                    HttpSession session) throws FileUploadException {
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

        ApplicationType applicationType = getApplicationTypeById(applicationId);

        List<ValidationErrors> allErrors = validateAndProcessFields(applicationType, mutableFields, submission.files());

        if (!allErrors.isEmpty()) {
            return allErrors;
        }

        // Get the authenticated User from the session
        Long userId = getAndValidateAuthenticatedUserId();

        // No need to convert back to JsonNode since mutableFields is already a JsonNode
        applicationRepository.save(applicationMapper.toApplication(applicationType, mutableFields, userId));

        return List.of();
    }

    public ApplicationType getApplicationTypeById(Long applicationTypeId) {
        return applicationTypeRepository.findById(applicationTypeId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application type not found"));
    }

    public List<ValidationErrors> validateAndProcessFields(ApplicationType applicationType,
                                                           ObjectNode mutableFields,
                                                           List<MultipartFile> files) throws FileUploadException {

        List<ValidationErrors> allErrors = new ArrayList<>();
        for (ApplicationSection section: applicationType.getSections()) {
            for(ApplicationFields field: section.getFields()) {
                String key = field.getKey();
                String fieldName = field.getFieldName();
                String fieldType = field.getFieldType().name();
                JsonNode submittedValue = mutableFields.get(key);

                if(field.getRequired() && submittedValue != null) {
                    allErrors.add(new ValidationErrors(fieldName,"Field " + fieldName + " is required"));
                    continue;
                }
                if(submittedValue != null && !submittedValue.isMissingNode()) {
                    ValidatorStrategy validator =  fieldValidatorFactory.getStrategy(fieldType);

                    List<ValidationErrors> fieldErrors = validator.validate(field, submittedValue);
                    allErrors.addAll(fieldErrors);

                    if(fieldErrors.isEmpty() && validator instanceof FileValidator) {
                        String savePath = FileValidator.saveFile(submittedValue,files);
                        mutableFields.put(key, savePath);
                    }

                }

            }
        }
        return allErrors;
    }

    private Long getAndValidateAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication: {}, Principal: {}", authentication,
                authentication != null ? authentication.getPrincipal() : null);

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User is not authenticated");
        }

        return userDetails.getUserId();
    }

}
