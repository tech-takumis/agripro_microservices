package com.hashjosh.application.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.dto.validation.ValidationErrors;
import com.hashjosh.application.model.ApplicationField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidator implements ValidatorStrategy {


    @Override
    public List<ValidationErrors> validate(ApplicationField field, JsonNode value) {


        List<ValidationErrors> errors = new ArrayList<>();

        // Implement proper validation logic of file uploads here

        return errors;
    }
}

