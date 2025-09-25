package com.hashjosh.kafkacommon.user;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegistrationContract {
    private String username;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
}
