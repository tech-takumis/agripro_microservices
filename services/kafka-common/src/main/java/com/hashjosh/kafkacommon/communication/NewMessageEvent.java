package com.hashjosh.kafkacommon.communication;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class NewMessageEvent {
    private UUID messageId;
    private UUID conversationId;
    private UUID senderId;
    private UUID receiverId;
    private String text;
    private List<UUID> attachments;
    private LocalDateTime timestamp;
}
