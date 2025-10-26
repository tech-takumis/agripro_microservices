package com.hashjosh.application.validators;

import com.hashjosh.application.dto.validation.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.model.ApplicationField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationValidator implements ValidatorStrategy{
    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {
        // need to implement location validation
        return null;
    }
}
