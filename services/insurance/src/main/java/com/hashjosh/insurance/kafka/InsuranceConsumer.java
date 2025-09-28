package com.hashjosh.insurance.kafka;


import com.hashjosh.constant.EventType;
import com.hashjosh.insurance.enums.ClaimStatus;
import com.hashjosh.insurance.model.Claim;
import com.hashjosh.insurance.repository.ClaimRepository;
import com.hashjosh.kafkacommon.application.ApplicationContract;
import com.hashjosh.kafkacommon.application.ApplicationSubmissionContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsuranceConsumer {

    private final ClaimRepository claimRepository;

    @KafkaListener(topics = "application-events")
    public void consumeApplicationEvent(ApplicationSubmissionContract applicationSubmissionContract) {
        log.info("Application event received: {}", applicationSubmissionContract);

        if(EventType.APPLICATION_SUBMITTED.equals(applicationSubmissionContract.getEventType())){
            handleVerifiedApplication(applicationSubmissionContract);
        }
    }

    // TODO: Need to  implement the claim amount in verification service do not hard code the amount here
    private void handleVerifiedApplication(ApplicationSubmissionContract applicationSubmissionContract) {
        Claim claim = new Claim();

        claim.setApplicationId(claim.getApplicationId());
        claim.setClaimAmount(1000.0);
        claim.setPayoutStatus(ClaimStatus.PENDING);

        claimRepository.save(claim);

        log.info("Application claim created::: {}", claim);
    }
}
