package com.hashjosh.application.kafka;

import com.hashjosh.application.exceptions.ApplicationNotFoundException;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationConsumer {

    private final ApplicationRepository applicationRepository;

    @KafkaListener(topics = "application-events")
    public void consumeSubmission(ApplicationVerificationContract contract) {
        log.info("Application event received: {}", contract);

        Application application = applicationRepository.findById(contract.getApplicationId())
                        .orElseThrow(() -> new ApplicationNotFoundException("Application not found", HttpStatus.NOT_FOUND.value()));

        application.setStatus(contract.getStatus());
        applicationRepository.save(application);

        log.info("Application {}: {}",contract.getStatus(), application);
    }


}
