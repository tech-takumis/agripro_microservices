package com.hashjosh.communication.controller;

import com.hashjosh.communication.dto.MessageDto;
import com.hashjosh.communication.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{farmerId}/messages")
    public ResponseEntity<List<MessageDto>> getMessagesWithAgricultureStaff(
            @PathVariable UUID farmerId
    ) {
        log.info("Fetching messages for farmer with ID: {}", farmerId);
        return new ResponseEntity<>(chatService.getAllMessagesWithAgricultureStaff(farmerId), HttpStatus.OK);
    }
}
