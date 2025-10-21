package com.hashjosh.communication.service;

import com.hashjosh.communication.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

}
