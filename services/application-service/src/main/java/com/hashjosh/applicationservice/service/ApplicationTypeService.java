package com.hashjosh.applicationservice.service;

import com.hashjosh.applicationservice.dto.ApplicationFieldsRequestDto;
import com.hashjosh.applicationservice.dto.ApplicationSectionRequestDto;
import com.hashjosh.applicationservice.dto.ApplicationTypeRequestDto;
import com.hashjosh.applicationservice.dto.ApplicationTypeResponseDto;
import com.hashjosh.applicationservice.mapper.ApplicationFieldMapper;
import com.hashjosh.applicationservice.mapper.ApplicationTypeMapper;
import com.hashjosh.applicationservice.model.ApplicationFields;
import com.hashjosh.applicationservice.model.ApplicationSection;
import com.hashjosh.applicationservice.model.ApplicationType;
import com.hashjosh.applicationservice.repository.ApplicationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationTypeService {

    private final ApplicationTypeRepository applicationTypeRepository;
    private final ApplicationSectionService applicationSectionService;
    private final ApplicationFieldsService applicationFieldService;
    private final ApplicationTypeMapper applicationTypeMapper;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public ApplicationTypeResponseDto create(ApplicationTypeRequestDto dto) {
        ApplicationType applicationType = applicationTypeMapper.toApplicationType(dto);
        ApplicationType savedApplicationType = applicationTypeRepository.save(applicationType);

        List<ApplicationSection> applicationSections = new ArrayList<>();
        for (ApplicationSectionRequestDto sectionRequestDto : dto.sections()) {
            ApplicationSection section = applicationSectionService.create(sectionRequestDto, savedApplicationType);

            List<ApplicationFields> applicationFields = new ArrayList<>();
            for (ApplicationFieldsRequestDto field : sectionRequestDto.fields()) {
                ApplicationFields savedField = applicationFieldService.create(field, section.getId());
                applicationFields.add(savedField);
            }

            section.setFields(applicationFields);
            applicationSections.add(section);
        }

        applicationType.setSections(applicationSections);
        return applicationTypeMapper.toApplicationResponse(applicationType);
    }

    public List<ApplicationTypeResponseDto> findAll() {
            return applicationTypeRepository.findAll().stream()
                    .map(applicationTypeMapper::toApplicationResponse)
                    .collect(Collectors.toList());
    }

    public ApplicationTypeResponseDto findById(Long id) {
        return applicationTypeMapper.toApplicationResponse(applicationTypeRepository.findById(id).orElse(null));
    }
}
