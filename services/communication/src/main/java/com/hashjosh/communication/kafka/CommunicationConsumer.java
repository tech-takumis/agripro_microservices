package com.hashjosh.communication.kafka;


import com.hashjosh.communication.entity.Conversation;
import com.hashjosh.communication.entity.DesignatedStaff;
import com.hashjosh.communication.entity.User;
import com.hashjosh.communication.repository.ConversationRepository;
import com.hashjosh.communication.repository.DesignatedStaffRepository;
import com.hashjosh.communication.repository.UserRepository;
import com.hashjosh.constant.communication.enums.ConversationType;
import com.hashjosh.constant.communication.enums.ServiceType;
import com.hashjosh.kafkacommon.agriculture.AgricultureRegistrationContract;
import com.hashjosh.kafkacommon.farmer.FarmerRegistrationContract;
import com.hashjosh.kafkacommon.pcic.PcicRegistrationContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommunicationConsumer {

    private final UserRepository userRepository;
    private final DesignatedStaffRepository designatedStaffRepository;
    private final ConversationRepository conversationRepository;

    @KafkaListener(topics = "farmer-events",groupId = "communication-service")
    public void subscribeFarmerRegistration(@Payload FarmerRegistrationContract event){
        log.info("Received Farmer Registration Event: {}", event);

        User user = new User();
        user.setServiceType(ServiceType.FARMER);
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Let's create the conversation between Farmer and Agriculture staff
        UUID receiverId = designatedStaffRepository.findByServiceType(ServiceType.AGRICULTURE)
                .map(DesignatedStaff::getUserId)
                .orElseThrow(() -> new IllegalStateException("No designated Agriculture staff"));

        log.info("Creating conversation between Farmer (ID: {}) and Agriculture staff (ID: {})", user.getId(), receiverId);
        Conversation conversion = Conversation.builder()
                .senderId(user.getId())
                .receiverId(receiverId)
                .type(ConversationType.FARMER_AGRICULTURE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        conversationRepository.save(conversion);
        log.info("Saved Farmer Registration Event: {}", event);
    }

    @KafkaListener(topics = "agriculture-events", groupId = "communication-service")
    public void subscribeAgricultureRegistration(@Payload AgricultureRegistrationContract event){
        log.info("Received Agriculture Registration Event: {}", event);

        User user = new User();
        user.setServiceType(ServiceType.AGRICULTURE);
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("Save Agriculture Registration event {}", event);
    }

    @KafkaListener(topics = "pcic-events", groupId = "communication-service")
    public void subscribePcicRegistration(@Payload PcicRegistrationContract event){
        log.info("Received Pcic Registration Event: {}", event);
        User user = new User();
        user.setServiceType(ServiceType.PCIC);
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Save PCIC Registration event {}",event);
    }
}
