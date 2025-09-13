package com.hashjosh.application.validators;

import com.hashjosh.application.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.model.ApplicationField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SignatureValidator implements ValidatorStrategy{
    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {
        // Need to implement signature validation
        return null;
    }
}
