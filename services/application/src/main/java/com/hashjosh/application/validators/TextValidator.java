package com.hashjosh.application.validators;

import com.hashjosh.application.dto.validation.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.model.ApplicationField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextValidator implements ValidatorStrategy {
    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {

        if(!field.getRequired() && (value == null || value.isNull())){
            return new ArrayList<>();
        }
        List<ValidationErrors> errors = new ArrayList<>();
        if (!value.isTextual()) {
           errors.add(new ValidationErrors(
                    field.getKey(),
                    "Field must be a text value (TEXT)"
            ));
        }
        return errors;
    }
}
