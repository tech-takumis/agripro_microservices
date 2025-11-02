package com.hashjosh.kafkacommon.agriculture;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewInvitationEvent {
    private String email;
    private String token;
    private LocalDateTime expiryDate;
}
