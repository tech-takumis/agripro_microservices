package com.hashjosh.constant.communication;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Message text cannot be blank")
    private String text;
    private LocalDateTime sentAt;
}
