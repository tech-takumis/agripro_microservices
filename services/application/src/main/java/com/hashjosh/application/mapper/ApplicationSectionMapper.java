package com.hashjosh.application.mapper;

import com.hashjosh.application.dto.fields.ApplicationFieldsResponseDto;
import com.hashjosh.application.dto.sections.ApplicationSectionRequestDto;
import com.hashjosh.application.dto.sections.ApplicationSectionResponseDto;
import com.hashjosh.application.model.ApplicationSection;
import com.hashjosh.application.model.ApplicationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationSectionMapper {

    private final ApplicationFieldMapper applicationFieldMapper;

    public ApplicationSection toApplicationSection(ApplicationSectionRequestDto dto, ApplicationType savedApplicationType) {
        return ApplicationSection.builder()
                .title(dto.title())
                .applicationType(savedApplicationType)
                .build();
    }

    public ApplicationSectionResponseDto toApplicationSectionResponseDto(ApplicationSection applicationSection) {
        List<ApplicationFieldsResponseDto> fields = applicationSection.getFields()
                .stream().map(
                    applicationFieldMapper::toApplicationFieldResponseDto
                ).toList();

        return new ApplicationSectionResponseDto(
                applicationSection.getId(),
                applicationSection.getTitle(),
                fields

        );
    }

}
