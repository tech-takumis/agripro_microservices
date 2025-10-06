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
public class FileValidator implements ValidatorStrategy {

    private final DocumentServiceClient documentServiceClient;

    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {
        List<ValidationErrors> errors = new ArrayList<>();
        
        if (value == null || value.isNull()) {
            if (field.getRequired()) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Field '%s' is required", field.getFieldName())
                ));
            }
            return errors;
        }

        if (!value.isTextual()) {
            errors.add(new ValidationErrors(
                field.getKey(),
                String.format("Field '%s' must be a text value referencing a document ID", field.getFieldName())
            ));
            return errors;
        }

        String documentIdStr = value.asText().trim();
        if (documentIdStr.isEmpty()) {
            if (field.getRequired()) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Field '%s' cannot be empty", field.getFieldName())
                ));
            }
            return errors;
        }

        try {
            UUID documentId = UUID.fromString(documentIdStr);
            try {
                // Get the current authentication token from security context
                String token = getCurrentToken();
                DocumentResponse document = documentServiceClient.getDocument(token, documentId);

                
            } catch (HttpClientErrorException.NotFound e) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Document with ID '%s' not found", documentIdStr)
                ));
            } catch (FileUploadException e) {
                log.error("Error validating document with ID: " + documentIdStr, e);
                errors.add(new ValidationErrors(
                    field.getKey(),
                    String.format("Error validating document: %s", e.getMessage())
                ));
            }
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationErrors(
                field.getKey(),
                String.format("'%s' is not a valid document ID format", documentIdStr)
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

