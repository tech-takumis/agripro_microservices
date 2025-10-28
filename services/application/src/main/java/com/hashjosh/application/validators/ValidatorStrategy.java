package com.hashjosh.application.validators;

import com.hashjosh.application.dto.validation.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.application.model.ApplicationField;

import java.util.List;

public interface ValidatorStrategy{
    List<ValidationErrors> validate(ApplicationField fields, JsonNode value);

}
