package com.hashjosh.verification.kafka;


import com.hashjosh.constant.application.RecipientType;
import com.hashjosh.kafkacommon.application.ApplicationReceivedEvent;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import com.hashjosh.verification.entity.VerificationRecord;
import com.hashjosh.verification.enums.VerificationStatus;
import com.hashjosh.verification.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationConsumer {

    private final VerificationRepository verificationRepository;
    private final KafkaPublisher kafkaPublisher;

    @KafkaListener(topics = "application-lifecycle", groupId = "verification-group")
    public void listen(@Payload ApplicationSubmittedEvent event){
        log.info("Received ApplicationSubmittedEvent for applicationId: {}", event.getUploadedBY());
        // Add verification logic here
        if(event.getRecipientType() == RecipientType.AGRICULTURE){
            log.info("Performing agriculture-specific verification for applicationId: {}",event.getUploadedBY());
            // Agriculture-specific verification logic

            VerificationRecord record = VerificationRecord.builder()
                    .submissionId(event.getSubmissionId())
                    .uploadedBy(event.getUploadedBY())
                    .status(VerificationStatus.PENDING)
                    .build();

            log.info("Created VerificationRecord: {}", record);

            verificationRepository.save(record);


            ApplicationReceivedEvent receivedEvent = ApplicationReceivedEvent.builder()
                    .submissionId(event.getSubmissionId())
                    .uploadedBY(event.getUploadedBY())
                    .verificationStatus("Application Received for Verification")
                    .detials("Your application successfully received by agriculture for verification.")
                    .receivedAt(event.getSubmittedAt())
                    .build();

            log.info("Emitting ApplicationReceivedEvent: {}", receivedEvent);

            kafkaPublisher.publishEvent("application-lifecycle", receivedEvent);
        }
    }
}
