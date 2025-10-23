package com.hashjosh.identity.dto;

import com.hashjosh.constant.user.UserResponseDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String websocketToken;
    private UserResponseDTO user;
}

