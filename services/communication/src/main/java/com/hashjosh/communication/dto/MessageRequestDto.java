package com.hashjosh.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {
    private UUID senderId;
    private UUID receiverId;
    private String text;
    private String type; // FARMER_AGRICULTURE, AGRICULTURE_PCIC
    private Set<UUID> attachments;
    private LocalDateTime sentAt;
}
