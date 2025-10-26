package com.hashjosh.pcic.dto.auth;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticatedResponse{
    private String websocketToken;
    private AuthenticatedUser user;
}
