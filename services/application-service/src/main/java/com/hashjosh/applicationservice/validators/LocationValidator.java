package com.hashjosh.applicationservice.validators;

import com.hashjosh.applicationservice.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.applicationservice.model.ApplicationFields;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationValidator implements ValidatorStrategy{
    @Override
    public List<ValidationErrors> validate(ApplicationFields field, JsonNode value) {
        // need to implement location validation
        return null;
    }
}
