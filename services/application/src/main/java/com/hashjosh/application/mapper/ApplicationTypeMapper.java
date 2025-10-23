package com.hashjosh.application.mapper;

import com.hashjosh.application.dto.ApplicationSectionResponseDto;
import com.hashjosh.application.dto.ApplicationTypeRequestDto;
import com.hashjosh.application.dto.ApplicationTypeResponseDto;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.constant.application.RecipientType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationTypeMapper {
    private final ApplicationSectionMapper applicationSectionMapper;

    public ApplicationType toApplicationType(ApplicationTypeRequestDto dto) {
        return ApplicationType.builder()
                .name(dto.name())
                .recipientType(dto.recipient())
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
