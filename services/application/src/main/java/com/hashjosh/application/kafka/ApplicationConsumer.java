package com.hashjosh.application.kafka;

import com.hashjosh.application.exceptions.ApplicationNotFoundException;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationConsumer {

    private final ApplicationRepository applicationRepository;

    @KafkaListener(topics = "application-events")
    public void consumeSubmission(ApplicationSubmissionContract contract) {
        log.info("Application event received: {}", contract);

        Application application = applicationRepository.findById(contract.getApplicationId())
                        .orElseThrow(() -> new ApplicationNotFoundException("Application not found", HttpStatus.NOT_FOUND.value()));

        application.setStatus(contract.getStatus());
        applicationRepository.save(application);

        log.info("Application {}: {}",contract.getStatus(), application);
    }


}
