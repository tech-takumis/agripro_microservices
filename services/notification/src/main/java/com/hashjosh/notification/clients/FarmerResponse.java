package com.hashjosh.notification.clients;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class FarmerResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
