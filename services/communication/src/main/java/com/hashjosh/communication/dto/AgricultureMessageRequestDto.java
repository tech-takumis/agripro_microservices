package com.hashjosh.communication.dto;


import com.hashjosh.constant.communication.AttachmentRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgricultureMessageRequestDto {
    @NotBlank(message = "Message text cannot be blank")
    private String text;
    private UUID senderId;
    private Set<AttachmentRequest> attachments;
    private LocalDateTime sendAt;
}
