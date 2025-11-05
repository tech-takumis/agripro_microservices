package com.hashjosh.realtimegatewayservice.service;

import com.hashjosh.realtimegatewayservice.dto.NotificationRequestDTO;
import com.hashjosh.realtimegatewayservice.dto.NotificationResponseDTO;
import com.hashjosh.realtimegatewayservice.entity.Notification;
import com.hashjosh.realtimegatewayservice.exception.ApiException;
import com.hashjosh.realtimegatewayservice.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO requestDTO) {
        Notification notification = Notification.builder()
                .title(requestDTO.getTitle())
                .message(requestDTO.getMessage())
                .createdAt(LocalDateTime.now())
                .recipient("ALL")
                .read(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/application.notifications",
                savedNotification);

        return NotificationResponseDTO.builder()
                .id(savedNotification.getId())
                .title(savedNotification.getTitle())
                .message(savedNotification.getMessage())
                .time(savedNotification.getCreatedAt())
                .read(false)
                .build();
    }


    @Transactional
    public List<NotificationResponseDTO> getNotificationsForUser(String username) {

        List<Notification> notifications = notificationRepository.findByRecipientOrRecipient("ALL", username);

        return notifications.stream().map(notification ->
            NotificationResponseDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .time(notification.getCreatedAt())
                .read(notification.isRead())
                .build()
        ).toList();
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
