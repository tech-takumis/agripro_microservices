package com.hashjosh.applicationservice.mapper;

import com.hashjosh.applicationservice.dto.ApplicationFieldsRequestDto;
import com.hashjosh.applicationservice.dto.ApplicationFieldsResponseDto;
import com.hashjosh.applicationservice.enums.FieldType;
import com.hashjosh.applicationservice.model.ApplicationFields;
import com.hashjosh.applicationservice.model.ApplicationSection;
import org.springframework.stereotype.Component;

@Component
public class ApplicationFieldMapper {
    public ApplicationFields toApplicationField(ApplicationFieldsRequestDto dto, ApplicationSection applicationSection) {
        return ApplicationFields.builder()
                .key(dto.key())
                .fieldName(dto.fieldName())
                .fieldType(FieldType.valueOf(dto.fieldType().toUpperCase()))
                .required(dto.required())
                .defaultValue(dto.defaultValue())
                .validationRegex(dto.validationRegex())
                .applicationSection(applicationSection)
                .build();
    }

    public ApplicationFieldsResponseDto toApplicationFieldResponseDto(ApplicationFields applicationFields) {
        return new ApplicationFieldsResponseDto(
                applicationFields.getId(),
                applicationFields.getKey(),
                applicationFields.getFieldName(),
                applicationFields.getFieldType(),
                applicationFields.getRequired(),
                applicationFields.getDefaultValue(),
                applicationFields.getChoices(),
                applicationFields.getValidationRegex()
        );
    }
}
