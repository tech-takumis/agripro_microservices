package com.hashjosh.users.controller;

import com.hashjosh.kafkacommon.user.TenantType;
import com.hashjosh.users.dto.RegistrationResponse;
import com.hashjosh.users.dto.UserResponse;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.services.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/staff")
public class staffController {

    private final StaffService staffService;


    // All PCIC ROUTES
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(staffService.findAll());
    }

    @GetMapping("/type/{tenantType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable TenantType tenantType) {
        return ResponseEntity.ok(staffService.getUsersByType(tenantType));
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse>  getUserById(
            @PathVariable("user-id") UUID userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(staffService.getUserById(userId));
    }

}
