package com.hashjosh.application.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.clients.DocumentServiceClient;
import com.hashjosh.application.dto.validation.ValidationErrors;
import com.hashjosh.application.model.ApplicationField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignatureValidator implements ValidatorStrategy {


    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {
        List<ValidationErrors> errors = new ArrayList<>();

//        // Only check required and non-empty
//        if (field.getRequired()) {
//            if (value == null || value.isNull() || !value.isTextual() || value.asText().trim().isEmpty()) {
//                errors.add(new ValidationErrors(
//                    field.getKey(),
//                    String.format("Signature field '%s' is required", field.getFieldName())
//                ));
//                return errors;
//            }
//        }
//
//        // If not required and empty, skip further validation
//        if (value == null || value.isNull() || !value.isTextual() || value.asText().trim().isEmpty()) {
//            return errors;
//        }

        return errors;
    }
}