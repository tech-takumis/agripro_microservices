package com.example.agriculture.kafka;

import com.example.agriculture.entity.VerificationRecord;
import com.hashjosh.constant.verification.VerificationStatus;
import com.example.agriculture.repository.VerificationRecordRepository;
import com.hashjosh.kafkacommon.application.ApplicationSubmittedEvent;
import com.hashjosh.kafkacommon.application.VerificationStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgricultureConsumer {

    private final VerificationRecordRepository verificationRecordRepository;
    private final AgricultureProducer agricultureProducer;

    @KafkaListener(topics = "application-lifecycle", groupId = "agriculture-group")
    public void handleApplicationSubmitted(ApplicationSubmittedEvent event) {
       try{
           VerificationRecord record = VerificationRecord.builder()
                   .submissionId(event.getSubmissionId())
                   .uploadedBy(event.getUserId())
                   .status(VerificationStatus.PENDING)
                   .verificationType("Application Verification")
                   .build();
           verificationRecordRepository.save(record);
           agricultureProducer.publishEvent("application-lifecycle",
                   new VerificationStartedEvent(
                           event.getSubmissionId(),
                           event.getUserId(),
                           LocalDateTime.now()
                   ));

           log.info("Verification started for application: {}", event.getSubmissionId());

       }catch (Exception e){
           log.error("Failed to process ApplicationSubmittedEvent: {}", event.getSubmissionId(), e);
           throw e;
       }
    }

}
