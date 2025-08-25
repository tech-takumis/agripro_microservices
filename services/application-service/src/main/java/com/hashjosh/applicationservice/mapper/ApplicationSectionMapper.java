package com.hashjosh.applicationservice.mapper;

import com.hashjosh.applicationservice.dto.ApplicationFieldsResponseDto;
import com.hashjosh.applicationservice.dto.ApplicationSectionRequestDto;
import com.hashjosh.applicationservice.dto.ApplicationSectionResponseDto;
import com.hashjosh.applicationservice.model.ApplicationSection;
import com.hashjosh.applicationservice.model.ApplicationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

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
