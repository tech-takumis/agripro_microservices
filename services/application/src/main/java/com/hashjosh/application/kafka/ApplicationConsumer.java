package com.hashjosh.application.kafka;

import com.hashjosh.application.exceptions.ApplicationNotFoundException;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.repository.ApplicationRepository;
import com.hashjosh.constant.ApplicationStatus;
import com.hashjosh.constant.EventType;
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
                case  // Verification events
                     APPLICATION_APPROVED_BY_MA,
                     APPLICATION_REJECTED_BY_MA,
                     APPLICATION_APPROVED_BY_AEW,
                     APPLICATION_REJECTED_BY_AEW,

                     // Underwriter events
                     UNDER_REVIEW_BY_UNDERWRITER,
                     APPLICATION_APPROVED_BY_UNDERWRITER,
                     APPLICATION_REJECTED_BY_UNDERWRITER,

                     // Adjuster events
                     UNDER_REVIEW_BY_ADJUSTER,
                     APPLICATION_APPROVED_BY_ADJUSTER,
                     APPLICATION_REJECTED_BY_ADJUSTER,

                     // Insurance service events
                     POLICY_ISSUED,
                     CLAIM_APPROVED -> handleApplicationChangeStatus(contract);
                default -> log.error("Invalid event type: {}", contract);
            }
        }catch (Exception e){
            log.error("❌ Failed to process application event: {}", contract, e);
            throw  e;
        }
    }

    private void handleApplicationChangeStatus(ApplicationContract contract) {

        Application application = findApplicationById(contract.getApplicationId());

        application.setStatus(contract.getStatus());
        applicationRepository.save(application);

        log.info("Application {}: {}",contract.getStatus(), application);
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
