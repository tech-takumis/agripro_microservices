package com.hashjosh.application.kafka;

import com.hashjosh.application.enums.ApplicationStatus;
import com.hashjosh.application.exceptions.ApplicationNotFoundException;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationConsumer {

    private final ApplicationRepository applicationRepository;

    @KafkaListener(topics = "application-events")
    public void cosumeApplicationEvent(ApplicationContract contract) {
        log.info("Application event received: {}", contract);

        try{

            switch (contract.eventType()){
                case "application-rejected" -> handleApplicationRejected(contract);
                case "application-verified" -> handleApplicationVerified(contract);
                default -> log.error("Invalid event type: {}", contract);
            }

        }catch (Exception e){
            log.error("❌ Failed to process application event: {}", contract, e);
            throw  e;

        }
    }

    private void handleApplicationVerified(ApplicationContract contract) {

        Application application = findApplicationById(contract.applicationId());

        application.setStatus(ApplicationStatus.valueOf(contract.payload().status()));
        applicationRepository.save(application);

        log.info("Application verified: {}", application);
    }

    private void handleApplicationRejected(ApplicationContract contract) {

       Application application = findApplicationById(contract.applicationId());

        application.setStatus(ApplicationStatus.valueOf(contract.payload().status()));
        applicationRepository.save(application);

        log.info("Application rejected: {}", application);
    }

    private Application findApplicationById(UUID applicationId) {
        return  applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found with id "+ applicationId,
                        HttpStatus.BAD_REQUEST.value(),
                        "Application consume does not exist"
                ));
    }
}
