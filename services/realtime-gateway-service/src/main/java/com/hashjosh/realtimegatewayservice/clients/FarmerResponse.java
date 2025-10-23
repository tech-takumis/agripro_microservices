package com.hashjosh.realtimegatewayservice.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
