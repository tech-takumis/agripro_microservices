package com.hashjosh.application.kafka;

import com.hashjosh.application.exceptions.ApplicationNotFoundException;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.kafkacommon.application.ApplicationContract;
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
    public void consumeApplicationEvent(ApplicationContract contract) {
        log.info("Application event received: {}", contract);

        try{
            switch (contract.getEventType()){
                case "application_approved_by_municipal_agriculturist",
                     "application_rejected_by_municipal_agriculturist",
                     "application_canceled_by_municipal_agriculturist",
                     "pcic_reject_application",
                     "pcic_review_application",
                     "pcic_policy_issued",
                     "pcic_claim_approved"
                        -> handleApplicationChangeStatus(contract);
                default -> log.error("Invalid event type: {}", contract);
            }
        }catch (Exception e){
            log.error("❌ Failed to process application event: {}", contract, e);
            throw  e;
        }
    }

    private void handleApplicationChangeStatus(ApplicationContract contract) {

        Application application = findApplicationById(contract.getApplicationId());

        application.setStatus(ApplicationStatus.valueOf(contract.getPayload().getStatus()));
        applicationRepository.save(application);

        log.info("Application {}: {}",contract.getPayload().getStatus(), application);
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
