package com.hashjosh.application.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.configs.CustomUserDetails;
import com.hashjosh.application.dto.ValidationErrors;
import com.hashjosh.application.exceptions.FileUploadException;
import com.hashjosh.application.model.ApplicationField;
import com.hashjosh.constant.document.dto.DocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignatureValidator implements ValidatorStrategy {

    private final DocumentServiceClient documentServiceClient;
    private static final String DOCUMENT_PREFIX = "signature:";

    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {
        List<ValidationErrors> errors = new ArrayList<>();
        
        if (value == null || value.isNull()) {
            if (field.getRequired()) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Signature field '%s' is required", field.getFieldName())
                ));
            }
            return errors;
        }

        if (!value.isTextual()) {
            errors.add(new ValidationErrors(
                field.getKey(),
                String.format("Signature field '%s' must be a text value with format 'signature:<UUID>'", field.getFieldName())
            ));
            return errors;
        }

        String signatureValue = value.asText().trim();
        
        // Validate empty value for required field
        if (signatureValue.isEmpty()) {
            if (field.getRequired()) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Signature field '%s' cannot be empty", field.getFieldName())
                ));
            }
            return errors;
        }

        // Validate document prefix
        if (!signatureValue.startsWith(DOCUMENT_PREFIX)) {
            errors.add(new ValidationErrors(
                field.getKey(),
                String.format("Signature field '%s' must start with 'signature:'", field.getFieldName())
            ));
            return errors;
        }

        // Extract and validate UUID
        String documentIdStr = signatureValue.substring(DOCUMENT_PREFIX.length());
        try {
            UUID documentId = UUID.fromString(documentIdStr);
            try {
                String token = getCurrentToken();
                DocumentResponse document = documentServiceClient.getDocument(token, documentId);
                
            } catch (HttpClientErrorException.NotFound e) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Signature document with ID '%s' not found", documentIdStr)
                ));
            } catch (FileUploadException e) {
                log.error("Error validating signature document with ID: " + documentIdStr, e);
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Error validating signature document: %s", e.getMessage())
                ));
            }
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationErrors(
                field.getKey(),
                String.format("'%s' is not a valid UUID format for signature document", documentIdStr)
            ));
        }

        return errors;
    }
    
    private String getCurrentToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof CustomUserDetails)
                .map(CustomUserDetails.class::cast)
                .map(CustomUserDetails::getToken)
                .orElseThrow(() -> new SecurityException("No authentication token found in security context"));
    }
}