package com.hashjosh.kafkacommon.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPasswordResetEvent {
    private String firstName;
    private String lastName;
    private String email;
    private String token;
}
