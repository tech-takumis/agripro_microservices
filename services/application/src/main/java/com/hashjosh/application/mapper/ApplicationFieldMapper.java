package com.hashjosh.application.mapper;

import com.hashjosh.application.dto.fields.ApplicationFieldsRequestDto;
import com.hashjosh.application.dto.fields.ApplicationFieldsResponseDto;
import com.hashjosh.application.enums.FieldType;
import com.hashjosh.application.model.ApplicationField;
import com.hashjosh.application.model.ApplicationSection;
import org.springframework.stereotype.Component;

@Component
public class ApplicationFieldMapper {
    public ApplicationField toApplicationField(ApplicationFieldsRequestDto dto, ApplicationSection applicationSection) {
        return ApplicationField.builder()
                .key(dto.key())
                .fieldName(dto.fieldName())
                .fieldType(FieldType.valueOf(dto.fieldType().toUpperCase()))
                .required(dto.required())
                .defaultValue(dto.defaultValue())
                .validationRegex(dto.validationRegex())
                .applicationSection(applicationSection)
                .build();
    }

    public ApplicationFieldsResponseDto toApplicationFieldResponseDto(ApplicationField applicationFields) {
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
