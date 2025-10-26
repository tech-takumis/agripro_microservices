package com.hashjosh.application.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashjosh.application.dto.validation.ValidationErrors;
import com.hashjosh.application.model.ApplicationField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JsonValidator implements ValidatorStrategy {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {

        if(!field.getRequired() && (value == null || value.isNull())){
            return new ArrayList<>();
        }
        List<ValidationErrors> errors = new ArrayList<>();
        
        if (value == null || value.isNull()) {
            if (field.getRequired()) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    "Field is required"
                ));
            }
            return errors;
        }
        
        // If the value is a string, try to parse it as JSON
        if (value.isTextual()) {
            try {
                // Try to parse the string as JSON
                objectMapper.readTree(value.asText());
            } catch (JsonProcessingException e) {
                errors.add(new ValidationErrors(
                    field.getKey(),
                    "Invalid JSON format: " + e.getOriginalMessage()
                ));
            }
        } 
        // If it's already a JSON object or array, it's valid
        else if (!value.isObject() && !value.isArray()) {
            errors.add(new ValidationErrors(
                field.getKey(),
                "Field must be a valid JSON object or array"
            ));
        }
        
        return errors;
    }
}
