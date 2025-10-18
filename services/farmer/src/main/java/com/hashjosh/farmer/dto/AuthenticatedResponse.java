package com.hashjosh.farmer.dto;

import com.hashjosh.farmer.entity.FarmerProfile;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticatedResponse {
    private UUID id;
    private String accessToken;
    private String refreshToken;
    private String websocketToken;
    private AuthUserResponse user;
}
