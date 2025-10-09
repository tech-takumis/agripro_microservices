package com.hashjosh.communication.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MessageDto {
    private UUID conversationId;
    private UUID senderId;
    private UUID recipientId;
    private String text;
    private JsonNode attachments; // From document service
    private LocalDateTime timestamp;
}
