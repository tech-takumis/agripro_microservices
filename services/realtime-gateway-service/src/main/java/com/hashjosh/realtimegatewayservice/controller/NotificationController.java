package com.hashjosh.realtimegatewayservice.controller;
import com.hashjosh.realtimegatewayservice.dto.NotificationRequestDTO;
import com.hashjosh.realtimegatewayservice.dto.NotificationResponseDTO;
import com.hashjosh.realtimegatewayservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{username}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsForUser(
            @PathVariable String username
    ) {
        List<NotificationResponseDTO> response = notificationService.getNotificationsForUser(username);
        return ResponseEntity.ok(response);
    }

}
