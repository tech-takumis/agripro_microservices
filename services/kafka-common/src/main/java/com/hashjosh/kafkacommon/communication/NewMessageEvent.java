package com.hashjosh.kafkacommon.communication;

import com.hashjosh.constant.communication.enums.ConversationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewMessageEvent {
    private UUID messageId;
    private UUID senderId;
    private UUID receiverId;
    private ConversationType conversationType;
    private String text;
    private List<AttachmentResponse> attachmentResponses;
    private LocalDateTime timestamp;
}
