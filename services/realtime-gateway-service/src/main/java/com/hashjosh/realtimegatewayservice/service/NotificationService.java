package com.hashjosh.realtimegatewayservice.service;

import com.hashjosh.realtimegatewayservice.dto.NotificationRequestDTO;
import com.hashjosh.realtimegatewayservice.dto.NotificationResponseDTO;
import com.hashjosh.realtimegatewayservice.entity.Notification;
import com.hashjosh.realtimegatewayservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
        Notification notification = Notification.builder()
                .title(requestDTO.getTitle())
                .message(requestDTO.getMessage())
                .createdAt(LocalDateTime.now())
                .recipient("ALL")
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return NotificationResponseDTO.builder()
                .id(savedNotification.getId())
                .title(savedNotification.getTitle())
                .message(savedNotification.getMessage())
                .time(savedNotification.getCreatedAt())
                .read(false)
                .build();
    }

    public List<NotificationResponseDTO> getNotificationsForUser(String username) {

        List<Notification> notifications = notificationRepository.findByRecipientOrRecipient("ALL", username);

        List<NotificationResponseDTO> responses = notifications.stream().map(notification ->
            NotificationResponseDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .time(notification.getCreatedAt())
                .read(false)
                .build()
        ).toList();
        return responses;
    }
}
