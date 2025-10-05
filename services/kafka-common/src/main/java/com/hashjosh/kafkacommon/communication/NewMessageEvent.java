package com.hashjosh.kafkacommon.communication;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class NewMessageEvent {
    private UUID messageId;
    private UUID conversationId;
    private UUID senderId;
    private String text;
    private JsonNode attachments;
    private LocalDateTime timestamp;
}
