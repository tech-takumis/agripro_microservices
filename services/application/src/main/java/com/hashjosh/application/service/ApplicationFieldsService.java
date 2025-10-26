package com.hashjosh.application.service;

import com.hashjosh.application.dto.fields.ApplicationFieldsRequestDto;
import com.hashjosh.application.mapper.ApplicationFieldMapper;
import com.hashjosh.application.model.ApplicationField;
import com.hashjosh.application.model.ApplicationSection;
import com.hashjosh.application.repository.ApplicationFieldsRepository;
import com.hashjosh.application.repository.ApplicationSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApplicationFieldsService {

    private final ApplicationFieldMapper applicationFieldMapper;
    private final ApplicationFieldsRepository applicationFieldsRepository;
    private final ApplicationSectionRepository applicationSectionRepository;

    public ApplicationFieldsService(ApplicationFieldMapper applicationFieldMapper,
                                    ApplicationFieldsRepository applicationFieldsRepository,
                                    ApplicationSectionRepository applicationSectionRepository) {
        this.applicationFieldMapper = applicationFieldMapper;
        this.applicationFieldsRepository = applicationFieldsRepository;
        this.applicationSectionRepository = applicationSectionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ApplicationField create(ApplicationFieldsRequestDto dto, UUID sectionId) {
        ApplicationSection applicationSection = applicationSectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Application section not found"));
        ApplicationField applicationFields = applicationFieldMapper.toApplicationField(dto, applicationSection);
        return applicationFieldsRepository.save(applicationFields);
    }
}