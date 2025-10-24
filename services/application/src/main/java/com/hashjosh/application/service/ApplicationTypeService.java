package com.hashjosh.application.service;

import com.hashjosh.application.dto.ApplicationFieldsRequestDto;
import com.hashjosh.application.dto.ApplicationSectionRequestDto;
import com.hashjosh.application.dto.ApplicationTypeRequestDto;
import com.hashjosh.application.dto.ApplicationTypeResponseDto;
import com.hashjosh.application.dto.batch.BatchResponseDTO;
import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.application.mapper.ApplicationTypeMapper;
import com.hashjosh.application.model.*;
import com.hashjosh.application.repository.ApplicationProviderRepository;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import com.hashjosh.application.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public List<ApplicationTypeResponseDto> findAll() {
            return applicationTypeRepository.findAll().stream()
                    .map(applicationTypeMapper::toApplicationResponse)
                    .collect(Collectors.toList());
    }

    public ApplicationTypeResponseDto findById(UUID id) {
        return applicationTypeMapper.toApplicationResponse(applicationTypeRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Application type not found")));
    }

}
