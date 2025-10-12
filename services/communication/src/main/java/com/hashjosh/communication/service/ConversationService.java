package com.hashjosh.communication.service;

import com.hashjosh.communication.entity.Conversation;
import com.hashjosh.communication.entity.DesignatedStaff;
import com.hashjosh.communication.repository.ConversationRepository;
import com.hashjosh.communication.repository.DesignatedStaffRepository;
import com.hashjosh.constant.communication.enums.ConversationType;
import com.hashjosh.constant.communication.enums.ServiceType;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final DesignatedStaffRepository designatedStaffRepository;

    public Conversation createConversation(UUID senderId, ConversationType type) {
        UUID receiverId;
        if (type == ConversationType.FARMER_AGRICULTURE) {
            receiverId = designatedStaffRepository.findByServiceType(ServiceType.AGRICULTURE)
                    .map(DesignatedStaff::getUserId)
                    .orElseThrow(() -> new IllegalStateException("No designated Agriculture staff"));
        } else if (type == ConversationType.AGRICULTURE_PCIC) {
            receiverId = designatedStaffRepository.findByServiceType(ServiceType.PCIC)
                    .map(DesignatedStaff::getUserId)
                    .orElseThrow(() -> new IllegalStateException("No designated PCIC staff"));
        } else {
            throw new IllegalArgumentException("Invalid conversation type");
        }

        Conversation conversation = Conversation.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .type(type)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return conversationRepository.save(conversation);
    }
}
