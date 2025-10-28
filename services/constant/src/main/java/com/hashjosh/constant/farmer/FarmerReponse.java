package com.hashjosh.constant.farmer;

import lombok.*;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class FarmerReponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
