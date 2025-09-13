package com.hashjosh.insurance.kafka;


import com.hashjosh.insurance.enums.ClaimStatus;
import com.hashjosh.insurance.model.Claim;
import com.hashjosh.insurance.repository.ClaimRepository;
import com.hashjosh.kafkacommon.application.ApplicationContract;
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
    public void consumeApplicationEvent(ApplicationContract contract) {
        log.info("Application event received: {}", contract);

        if("application-verified".equals(contract.eventType())){
            handleVerifiedApplication(contract);
        }
    }

    // TODO: Need to  implement the claim amount in verification service do not hard code the amount here
    private void handleVerifiedApplication(ApplicationContract contract) {
        Claim claim = new Claim();

        claim.setApplicationId(claim.getApplicationId());
        claim.setClaimAmount(1000.0);
        claim.setPayoutStatus(ClaimStatus.PENDING);

        claimRepository.save(claim);

        log.info("Application claim created::: {}", claim);
    }
}
