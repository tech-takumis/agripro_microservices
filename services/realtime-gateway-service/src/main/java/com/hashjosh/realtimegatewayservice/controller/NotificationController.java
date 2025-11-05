package com.hashjosh.realtimegatewayservice.controller;
import com.hashjosh.realtimegatewayservice.dto.NotificationRequestDTO;
import com.hashjosh.realtimegatewayservice.dto.NotificationResponseDTO;
import com.hashjosh.realtimegatewayservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationRequestDTO requestDTO
    ) {
        NotificationResponseDTO response = notificationService.createNotification(requestDTO);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{recipientId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsForUser(
            @PathVariable("recipientId") String recipientId
    ) {
        List<NotificationResponseDTO> response = notificationService.getNotificationsForUser(recipientId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable("notificationId") UUID notificationId
    ) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable("notificationId") UUID notificationId
    ) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

}
