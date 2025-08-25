package com.hashjosh.applicationservice.validators;

import com.hashjosh.applicationservice.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.applicationservice.model.ApplicationFields;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MultiSelectValidator implements ValidatorStrategy{
    @Override
    public List<ValidationErrors> validate(ApplicationFields field, JsonNode value) {
        List<ValidationErrors> errors = new ArrayList<>();
        if (!value.isArray()){
            errors.add(new ValidationErrors(
                    field.getFieldName(),
                    "Field must be an array of text values (MULTI_SELECT)"
            ));
        }
        return errors;
    }
}
