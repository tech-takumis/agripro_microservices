package com.hashjosh.pcic.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String webSocketToken;
    private AuthenticatedUser user;
}
