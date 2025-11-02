package com.example.agriculture.mapper;

import com.example.agriculture.entity.InvitationToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InvitationMapper {
    public InvitationToken toInvitationEntity(String email,String token) {
        return InvitationToken.builder()
                .token(token)
                .email(email)
                .expiryDate(LocalDateTime.now().plusHours(5))
                .used(false)
                .build();
    }
}
