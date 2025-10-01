package com.hashjosh.kafkacommon.agriculture;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgricultureRegistrationContract {
    private UUID userId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
