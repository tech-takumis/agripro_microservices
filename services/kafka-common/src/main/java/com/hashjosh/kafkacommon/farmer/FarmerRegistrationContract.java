package com.hashjosh.kafkacommon.farmer;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FarmerRegistrationContract {
    private UUID userId;
    private String rsbsaId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;

}
