package com.hashjosh.identity.controller;


import com.hashjosh.constant.user.UserResponseDTO;
import com.hashjosh.identity.dto.LoginRequest;
import com.hashjosh.identity.dto.LoginResponse;
import com.hashjosh.identity.dto.UserRegistrationRequest;
import com.hashjosh.identity.entity.User;
import com.hashjosh.identity.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        User user = authService.registerUser(request);
        return ResponseEntity.ok("User registered successfully with ID: " + user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse httpResponse) {
        return ResponseEntity.ok(authService.login(request, httpRequest, httpResponse));
    }

    // ✅ Logout
    @PostMapping("/{userId}/logout")
    public ResponseEntity<Void> logout(@PathVariable UUID userId,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        authService.logout(request, response, userId);
        return ResponseEntity.noContent().build();
    }

    // ✅ Request password reset
    @PostMapping("/password/reset/request")
    public ResponseEntity<String> requestReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok("We send the request to your email");
    }

    // ✅ Perform password reset
    @PostMapping("/password/reset/confirm")
    public ResponseEntity<String> confirmReset(@RequestParam String token,
                                               @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password successfully reset.");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
        UserResponseDTO userResponseDTO = authService.getAuthenticatedUser();
        return ResponseEntity.ok(userResponseDTO);
    }
}
