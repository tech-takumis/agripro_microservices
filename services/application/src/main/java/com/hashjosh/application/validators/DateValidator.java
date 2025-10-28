package com.hashjosh.application.validators;

import com.hashjosh.application.dto.validation.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.model.ApplicationField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DateValidator implements ValidatorStrategy{

    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {

        if(!field.getRequired() && (value == null || value.isNull())){
            return new ArrayList<>();
        }
        if(field.getRequired() && (value == null || value.isNull())){
            List<ValidationErrors> errors = new ArrayList<>();
            errors.add(new ValidationErrors(
                    field.getKey(),
                    "Field is required"
            ));
            return errors;
        }


        List<ValidationErrors> errors = new ArrayList<>();
        if (!value.isTextual() || !value.asText().matches("\\d{4}-\\d{2}-\\d{2}")){
            errors.add(new ValidationErrors(
                    field.getKey(),
                    "Field must be a valid ISO date (YYYY-MM-DD)"
            ));
        }
        return errors;
    }
}
