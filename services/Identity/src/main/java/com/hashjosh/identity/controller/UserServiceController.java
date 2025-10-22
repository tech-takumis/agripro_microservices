package com.hashjosh.identity.controller;


import com.hashjosh.constant.user.UserResponseDTO;
import com.hashjosh.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserServiceController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listUsers(
            @RequestPart(required = false) String tenantKey
    ) {
        return ResponseEntity.ok(userService.findAll(tenantKey));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(userService.findById(id));
    }

}
