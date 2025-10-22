package com.hashjosh.communication.controller;

import com.hashjosh.communication.service.ChatService;
import com.hashjosh.constant.communication.MessageRequestDto;
import com.hashjosh.constant.communication.MessageResponseDto;
import com.hashjosh.constant.communication.enums.ConversationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{farmerId}/messages")
    public ResponseEntity<List<MessageResponseDto>> getMessagesWithAgricultureStaff(
            @PathVariable UUID farmerId
    ) {
        log.info("Fetching messages for farmer with ID: {}", farmerId);
        return new ResponseEntity<>(chatService.getAllMessagesWithAgricultureStaff(farmerId), HttpStatus.OK);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<MessageResponseDto> createMessage(
            @RequestPart("message") @Valid  MessageRequestDto messageRequestDto,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ){
        log.info("Creating new message: {}", messageRequestDto);
        MessageResponseDto createdMessage = chatService.createMessage(messageRequestDto,attachments);
        return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
    }


}
