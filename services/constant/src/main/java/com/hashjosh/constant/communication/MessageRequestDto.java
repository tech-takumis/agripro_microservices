package com.hashjosh.constant.communication;

import com.hashjosh.constant.communication.enums.ConversationType;
import com.hashjosh.constant.validation.EnumValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {
    private UUID senderId;
    private UUID receiverId;
    private String text;
    @EnumValidator(enumClass = ConversationType.class, message = "Conversation type must be valid")
    private ConversationType type;
    private LocalDateTime sentAt;
}
