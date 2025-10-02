package com.hashjosh.farmer.controller;


import com.hashjosh.farmer.config.CustomUserDetails;
import com.hashjosh.farmer.dto.*;
import com.hashjosh.farmer.entity.Farmer;
import com.hashjosh.farmer.service.AuthService;
import com.hashjosh.farmer.service.RefreshTokenService;
import com.hashjosh.jwtshareable.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/farmer/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> register(
            @RequestBody RegistrationRequest farmer
    ){

        Farmer user = authService.register(farmer);

        return ResponseEntity.ok(
                RegistrationResponse.builder()
                        .username(user.getUsername())
                        .message("User Registered Successfully")
                        .error(null)
                        .success(true)
                        .status(HttpStatus.CREATED.value())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest) {


        String clientIp = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);

        LoginResponse tokens = authService.login(request, clientIp, userAgent);

        return ResponseEntity.ok(tokens);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {

        log.info("Refresh and Access token received from logout route: access token: {} refresh token: {}",authorization,refreshToken);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid access token");
        }

        // ❌ Delete refresh token from DB (always do this)
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        return ResponseEntity.ok("Logged out successfully");
    }


    @GetMapping("/me")
    public ResponseEntity<AuthenticatedResponse> getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails customUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Farmer farmer = customUserDetails.getFarmer();
        if (farmer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        AuthenticatedResponse response = authService.getAuthenticatedUser(farmer);
        return ResponseEntity.ok(response);
    }

    private Cookie buildCookie(String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        return cookie;
    }

    private long jwtExpirySeconds(String token) {
        Claims claims = jwtService.getAllClaims(token);
        return (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
    }
}
