package com.hashjosh.application.validators;

import com.hashjosh.application.dto.validation.ValidationErrors;
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

        if(!field.getRequired() && (value == null || value.isNull())){
            return new ArrayList<>();
        }

        List<ValidationErrors> errors = new ArrayList<>();
        
        // First check if the value is textual
        if (!value.isTextual()) {
            errors.add(new ValidationErrors(
                    field.getKey(),
                    "Field must be a text value (SELECT)"
            ));
            return errors; // Return early if not text
        }

        // Get the choices JsonNode
        JsonNode choices = field.getChoices();
        if (choices == null) {
            errors.add(new ValidationErrors(
                    field.getKey(),
                    "No choices defined for field: " + field.getKey()
            ));
            return errors; // Return early if no choices
        }

        // Debug log the choices structure
        System.out.println("Choices for field " + field.getKey() + ": " + choices);

        // Check if choices is an array directly
        if (choices.isArray()) {
            return validateAgainstChoices(field, value.asText(), choices, errors);
        }
        
        // If not an array, try to get the array by field key
        JsonNode allowedChoices = choices.get(field.getKey());
        if (allowedChoices != null && allowedChoices.isArray()) {
            return validateAgainstChoices(field, value.asText(), allowedChoices, errors);
        }

        // If we get here, the choices structure is not as expected
        errors.add(new ValidationErrors(
                field.getKey(),
                "Invalid choices format for field: " + field.getKey() + ". Expected an array or an object with field key."
        ));
        return errors;
    }

    private List<ValidationErrors> validateAgainstChoices(ApplicationField field, String submittedValue, 
                                                         JsonNode allowedChoices, List<ValidationErrors> errors) {
        try {
            boolean isValid = StreamSupport.stream(allowedChoices.spliterator(), false)
                    .anyMatch(choice -> choice.asText().equalsIgnoreCase(submittedValue));

            if (!isValid) {
                errors.add(new ValidationErrors(
                        field.getKey(),
                        "Invalid value '" + submittedValue + "' for field '" + field.getFieldName() + 
                        "'. Allowed values: " + allowedChoices
                ));
            }
        } catch (Exception e) {
            errors.add(new ValidationErrors(
                    field.getKey(),
                    "Error validating field " + field.getKey() + ": " + e.getMessage()
            ));
        }
        return errors;
    }
}
