package com.hashjosh.constant.communication;

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
public class MessageResponseDto {
    private UUID messageId;
    private UUID senderId;
    private UUID receiverId;
    private String text;
    private String type;
    private Set<AttachmentResponseDto> attachments;
    private LocalDateTime sentAt;
}
