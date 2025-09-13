package com.hashjosh.application.validators;

import com.hashjosh.application.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.model.ApplicationField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SelectValidator implements ValidatorStrategy {
    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {
        List<ValidationErrors> errors = new ArrayList<>();
        if (!value.isTextual()) {
           errors.add(new ValidationErrors(
                   field.getKey(),
                   "Field must be a text value (SELECT)"
           ));
        }

        JsonNode allowedChoices = field.getChoices().get(field.getFieldName());

        if (allowedChoices == null || !allowedChoices.isArray()) {
            errors.add(new ValidationErrors(
                    field.getFieldName(),
                    "Field must have a valid set of choices (SELECT)"
            ));
        }

        String submitted = value.asText();
        boolean isValid = StreamSupport.stream(allowedChoices.spliterator(), false)
                .anyMatch(choice -> choice.asText().equalsIgnoreCase(submitted));

        if (!isValid) {
           errors.add(new ValidationErrors(
                   field.getFieldName(),
                   "Invalid value '" + submitted + "' for field '" + field.getFieldName() + "'. Allowed: " + allowedChoices
           ));
        }
        return errors;
    }
}
