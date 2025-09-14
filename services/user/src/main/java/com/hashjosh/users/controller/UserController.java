package com.hashjosh.users.controller;

import com.hashjosh.users.dto.UserResponse;
import com.hashjosh.users.entity.TenantType;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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