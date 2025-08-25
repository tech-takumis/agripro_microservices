package com.hashjosh.applicationservice.validators;

import com.hashjosh.applicationservice.dto.ValidationErrors;
import com.fasterxml.jackson.databind.JsonNode;
import com.hashjosh.applicationservice.model.ApplicationFields;

import java.util.List;

public interface ValidatorStrategy{
    List<ValidationErrors> validate(ApplicationFields fields, JsonNode value);

}
