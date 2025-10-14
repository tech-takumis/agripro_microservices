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

        User user = userRepository.findByEmail(event.getEmail())
                .map(existingUser -> {
                    log.info("User with email {} already exists, updating service type", event.getEmail());
                    existingUser.setServiceType(ServiceType.FARMER);
                    existingUser.setUsername(event.getUsername());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    log.info("Creating new user with email {}", event.getEmail());
                    User newUser = User.builder()
                            .serviceType(ServiceType.FARMER)
                            .username(event.getUsername())
                            .email(event.getEmail())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(newUser);
                });

        // Let's create the conversation between Farmer and Agriculture staff
        UUID receiverId = designatedStaffRepository.findByServiceType(ServiceType.AGRICULTURE)
                .map(DesignatedStaff::getUserId)
                .orElseThrow(() -> new IllegalStateException("No designated Agriculture staff"));

        log.info("Creating conversation between Farmer (ID: {}) and Agriculture staff (ID: {})", user.getId(), receiverId);
        Conversation conversation = Conversation.builder()
                .senderId(user.getId())
                .receiverId(receiverId)
                .type(ConversationType.FARMER_AGRICULTURE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        conversationRepository.save(conversation);
        log.info("Saved Farmer Registration Event: {}", event);
    }

    @KafkaListener(topics = "agriculture-events", groupId = "communication-service")
    public void subscribeAgricultureRegistration(@Payload AgricultureRegistrationContract event){
        log.info("Received Agriculture Registration Event: {}", event);

        User user = userRepository.findByEmail(event.getEmail())
                .map(existingUser -> {
                    log.info("User with email {} already exists, updating service type", event.getEmail());
                    existingUser.setServiceType(ServiceType.AGRICULTURE);
                    existingUser.setUsername(event.getUsername());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    log.info("Creating new user with email {}", event.getEmail());
                    User newUser = User.builder()
                            .serviceType(ServiceType.AGRICULTURE)
                            .username(event.getUsername())
                            .email(event.getEmail())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(newUser);
                });

        // If no designated Agriculture staff for farmer and agriculture conversation assigned the consume staff itself.
        if(designatedStaffRepository.findByServiceType(ServiceType.AGRICULTURE).isEmpty()){
            DesignatedStaff staff = new DesignatedStaff();
            staff.setServiceType(ServiceType.AGRICULTURE);
            staff.setUserId(user.getId());
            staff.setCreatedAt(LocalDateTime.now());
            designatedStaffRepository.save(staff);
        }

        // Let's create the conversation between Agriculture and PCIC staff
        UUID receiverId = designatedStaffRepository.findByServiceType(ServiceType.PCIC)
                .map(DesignatedStaff::getUserId)
                .orElseThrow(() -> new IllegalStateException("No designated PCIC staff"));

        log.info("Creating conversation between Agriculture (ID: {}) and PCIC staff (ID: {})", user.getId(), receiverId);
        Conversation conversation = Conversation.builder()
                .senderId(user.getId())
                .receiverId(receiverId)
                .type(ConversationType.AGRICULTURE_PCIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        conversationRepository.save(conversation);

        log.info("Save Agriculture Registration event {}", event);
    }

    @KafkaListener(topics = "pcic-events", groupId = "communication-service")
    public void subscribePcicRegistration(@Payload PcicRegistrationContract event){
        log.info("Received Pcic Registration Event: {}", event);

        User user = userRepository.findByEmail(event.getEmail())
                .map(existingUser -> {
                    log.info("User with email {} already exists, updating service type", event.getEmail());
                    existingUser.setServiceType(ServiceType.PCIC);
                    existingUser.setUsername(event.getUsername());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    log.info("Creating new user with email {}", event.getEmail());
                    User newUser = User.builder()
                            .serviceType(ServiceType.PCIC)
                            .username(event.getUsername())
                            .email(event.getEmail())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return userRepository.save(newUser);
                });

        // If no designated PCIC staff for agriculture and PCIC conversation assigned the consume staff itself.
        if(designatedStaffRepository.findByServiceType(ServiceType.PCIC).isEmpty()){
            DesignatedStaff staff = new DesignatedStaff();
            staff.setServiceType(ServiceType.PCIC);
            staff.setUserId(user.getId());
            staff.setCreatedAt(LocalDateTime.now());
            designatedStaffRepository.save(staff);
        }

        log.info("Save PCIC Registration event {}", event);
    }
}
