package com.hashjosh.application.service;

import com.hashjosh.application.dto.fields.ApplicationFieldsRequestDto;
import com.hashjosh.application.dto.sections.ApplicationSectionRequestDto;
import com.hashjosh.application.dto.type.ApplicationTypeRequestDto;
import com.hashjosh.application.dto.type.ApplicationTypeResponseDto;
import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.application.mapper.ApplicationTypeMapper;
import com.hashjosh.application.model.*;
import com.hashjosh.application.repository.ApplicationProviderRepository;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationTypeService {

    private final ApplicationTypeRepository applicationTypeRepository;
    private final ApplicationSectionService applicationSectionService;
    private final ApplicationFieldsService applicationFieldService;
    private final ApplicationTypeMapper applicationTypeMapper;
    private final ApplicationProviderRepository applicationProviderRepository;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public ApplicationTypeResponseDto create(ApplicationTypeRequestDto dto) {
        ApplicationProvider provider = applicationProviderRepository
                .findByName(dto.providerName())
                .orElseThrow(() -> ApiException.badRequest("Application provider not found"));

        ApplicationType applicationType = applicationTypeMapper.toApplicationType(dto);
        applicationType.setProvider(provider);
        ApplicationType savedApplicationType = applicationTypeRepository.save(applicationType);

        List<ApplicationSection> applicationSections = new ArrayList<>();
        for (ApplicationSectionRequestDto sectionRequestDto : dto.sections()) {
            ApplicationSection section = applicationSectionService.create(sectionRequestDto, savedApplicationType);

            List<ApplicationField> applicationFields = new ArrayList<>();
            for (ApplicationFieldsRequestDto field : sectionRequestDto.fields()) {
                ApplicationField savedField = applicationFieldService.create(field, section.getId());
                applicationFields.add(savedField);
            }

            section.setFields(applicationFields);
            applicationSections.add(section);
        }

        applicationType.setSections(applicationSections);
        return applicationTypeMapper.toApplicationResponse(applicationType);
    }

    @Transactional
    public List<ApplicationTypeResponseDto> findAll() {
            return applicationTypeRepository.findAll().stream()
                    .map(applicationTypeMapper::toApplicationResponse)
                    .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationTypeResponseDto findById(UUID id) {
        return applicationTypeMapper.toApplicationResponse(applicationTypeRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Application type not found")));
    }

    @Transactional
    public List<ApplicationTypeResponseDto> findByProviderName(String provider) {
        return applicationTypeRepository.findAllByProvider_Name(provider).stream()
                .map(applicationTypeMapper::toApplicationResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public void deleteById(UUID id) {
        ApplicationType applicationType = applicationTypeRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Application type not found"));
        applicationTypeRepository.delete(applicationType);
    }
}
