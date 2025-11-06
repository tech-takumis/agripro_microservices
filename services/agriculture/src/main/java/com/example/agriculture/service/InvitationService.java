package com.example.agriculture.service;

import com.example.agriculture.entity.InvitationToken;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.mapper.InvitationMapper;
import com.example.agriculture.repository.InvitationTokenRepository;
import com.hashjosh.kafkacommon.agriculture.NewInvitationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationTokenRepository invitationTokenRepository;
    private final InvitationMapper invitationMapper;
    private final AgricultureProducer producer;

    public void sendInvitationEmail(String email){
        String token = generateInvitationToken();
        InvitationToken invitationToken = invitationMapper.toInvitationEntity(email,token);
        InvitationToken savedToken = invitationTokenRepository.save(invitationToken);

        // Publish an email new invitation to consume by our notification service
        NewInvitationEvent event = NewInvitationEvent.builder()
                .token(savedToken.getToken())
                .email(savedToken.getEmail())
                .expiryDate(savedToken.getExpiryDate())
                .build();

        producer.publishEvent("agriculture-invitations",event);
    }


    public String generateInvitationToken(){
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void validateInvitationToken(String token){
        InvitationToken invitationToken = invitationTokenRepository.findByToken(token)
                .orElseThrow(() -> ApiException.notFound("Invalid invitation token"));

        if(invitationToken.isExpired()){
            throw ApiException.badRequest("Invitation token has expired");
        }

        if(invitationToken.isUsed()){
            throw ApiException.badRequest("Invitation token has already been used");
        }

    }

    public void markAsUsed(String token){
        invitationTokenRepository.findByToken(token)
                .ifPresent(t -> {
                    t.setUsed(true);
                    invitationTokenRepository.save(t);
                });
    }
}
