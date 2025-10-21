package com.hashjosh.communication.kafka;


import com.hashjosh.communication.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommunicationConsumer {

    private final ConversationRepository conversationRepository;


}
