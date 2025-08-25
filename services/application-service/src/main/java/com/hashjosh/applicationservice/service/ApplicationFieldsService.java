package com.hashjosh.applicationservice.service;

import com.hashjosh.applicationservice.dto.ApplicationFieldsRequestDto;
import com.hashjosh.applicationservice.mapper.ApplicationFieldMapper;
import com.hashjosh.applicationservice.model.ApplicationFields;
import com.hashjosh.applicationservice.model.ApplicationSection;
import com.hashjosh.applicationservice.repository.ApplicationFieldsRepository;
import com.hashjosh.applicationservice.repository.ApplicationSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    public ApplicationFields create(ApplicationFieldsRequestDto dto, Long sectionId) {
        ApplicationSection applicationSection = applicationSectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Application section not found"));
        ApplicationFields applicationFields = applicationFieldMapper.toApplicationField(dto, applicationSection);
        return applicationFieldsRepository.save(applicationFields);
    }
}