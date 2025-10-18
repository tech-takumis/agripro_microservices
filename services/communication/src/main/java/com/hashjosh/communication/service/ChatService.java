package com.hashjosh.communication.service;

import com.hashjosh.communication.dto.MessageResponseDto;
import com.hashjosh.communication.entity.Message;
import com.hashjosh.communication.mapper.MessageMapper;
import com.hashjosh.communication.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public List<MessageResponseDto> getAllMessagesWithAgricultureStaff(UUID farmerId) {
        List<Message> messages = messageRepository.findMessagesByFarmerIdAndConversationType(farmerId);

        return messages.stream()
                .map(messageMapper::toMessageResponseDto)
                .toList();
    }


}
