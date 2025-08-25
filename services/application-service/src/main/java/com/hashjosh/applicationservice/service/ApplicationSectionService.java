package com.hashjosh.applicationservice.service;

import com.hashjosh.applicationservice.dto.ApplicationSectionRequestDto;
import com.hashjosh.applicationservice.mapper.ApplicationSectionMapper;
import com.hashjosh.applicationservice.model.ApplicationSection;
import com.hashjosh.applicationservice.model.ApplicationType;
import com.hashjosh.applicationservice.repository.ApplicationSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationSectionService {

    private final ApplicationSectionMapper applicationSectionMapper;
    private final ApplicationSectionRepository applicationSectionRepository;

    public ApplicationSectionService(ApplicationSectionMapper applicationSectionMapper,
                                     ApplicationSectionRepository applicationSectionRepository) {
        this.applicationSectionMapper = applicationSectionMapper;
        this.applicationSectionRepository = applicationSectionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ApplicationSection create(ApplicationSectionRequestDto dto, ApplicationType savedApplicationType) {
        ApplicationSection applicationSection = applicationSectionMapper.toApplicationSection(dto, savedApplicationType);
        return applicationSectionRepository.save(applicationSection);
    }
}