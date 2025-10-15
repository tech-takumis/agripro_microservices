package com.hashjosh.communication.controller;

import com.hashjosh.communication.dto.DesignatedResponse;
import com.hashjosh.communication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communication")
@Slf4j
public class UserController {

    private final UserService userService;
    @GetMapping("/designated/agriculture/chat")
    public ResponseEntity<DesignatedResponse> findAgricultureDesignatedStaff() {
        log.info("Request to find agriculture designated staff");
        DesignatedResponse response = userService.findAgricultureDesignatedStaff();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/designated/pcic/chat")
    public ResponseEntity<DesignatedResponse> findPCICDesignatedStaff() {
        log.info("Request to find PCIC designated staff");
        DesignatedResponse response = userService.findPCICDesignatedStaff();
        return ResponseEntity.ok(response);
    }
}
