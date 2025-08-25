package com.hashjosh.applicationservice.mapper;

import com.hashjosh.applicationservice.dto.ApplicationSectionResponseDto;
import com.hashjosh.applicationservice.dto.ApplicationTypeRequestDto;
import com.hashjosh.applicationservice.dto.ApplicationTypeResponseDto;
import com.hashjosh.applicationservice.model.ApplicationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationTypeMapper {
    private final ApplicationSectionMapper applicationSectionMapper;
    private final ApplicationFieldMapper applicationFieldMapper;

    public ApplicationType toApplicationType(ApplicationTypeRequestDto dto) {
        return ApplicationType.builder()
                .name(dto.name())
                .description(dto.description())
                .layout(dto.layout())
                .build();
    }

    public ApplicationTypeResponseDto toApplicationResponse(ApplicationType applicationType) {
        List<ApplicationSectionResponseDto> sections = applicationType.getSections()
                .stream().map(
                    applicationSectionMapper::toApplicationSectionResponseDto
                ).toList();

        return  new ApplicationTypeResponseDto(
            applicationType.getId(),
                applicationType.getName(),
                applicationType.getDescription(),
                applicationType.getLayout(),
                sections

        );
    }
}
