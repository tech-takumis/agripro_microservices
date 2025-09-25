package com.hashjosh.users.controller;

import com.hashjosh.users.dto.UserResponse;
import com.hashjosh.kafkacommon.user.TenantType;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/type/{userType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable TenantType tenantType) {
        return ResponseEntity.ok(userService.getUsersByType(tenantType));
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse>  getUserById(
            @PathVariable("user-id") UUID userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

}