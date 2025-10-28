package com.hashjosh.pcic.controller;


import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.pcic.config.CustomUserDetails;
import com.hashjosh.pcic.dto.auth.*;
import com.hashjosh.pcic.entity.*;
import com.hashjosh.pcic.exception.ApiException;
import com.hashjosh.pcic.service.AuthService;
import com.hashjosh.pcic.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pcic/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> register(
            @RequestBody @Valid RegistrationRequest request
    ){

        Pcic pcic = authService.register(request);

        return ResponseEntity.ok(
                RegistrationResponse.builder()
                        .username(pcic.getUsername())
                        .message("User Registered Successfully")
                        .success(true)
                        .status(HttpStatus.CREATED.value())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String clientIp = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);

        LoginResponse tokens = authService.login(request, clientIp, userAgent);

        Cookie accessCookie = buildCookie("ACCESS_TOKEN", tokens.getAccessToken(),
                    (int) (jwtExpirySeconds(tokens.getAccessToken())));
        Cookie refreshCookie = buildCookie("REFRESH_TOKEN", tokens.getRefreshToken(),
                    (int) Duration.ofDays(1).toSeconds());

        httpResponse.addCookie(accessCookie);
        httpResponse.addCookie(refreshCookie);

        AuthenticatedResponse response = AuthenticatedResponse.builder()
                .websocketToken(tokens.getWebSocketToken())
                .user(tokens.getUser())
                .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken,
            HttpServletResponse response) {

        log.info("Refresh and Access token received from logout route: access token: {} refresh token: {}",authorization,refreshToken);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid access token");
        }

        // 🔑 Extract access token & tenantId
        String accessToken = authorization.substring(7);

        // ❌ Delete refresh token from DB (always do this)
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        Cookie accessCookie = buildCookie("ACCESS_TOKEN", null,0);
        Cookie refreshCookie = buildCookie("REFRESH_TOKEN", null,0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

            // For farmers: only remove refresh token from DB
            return ResponseEntity.ok("Logged out: refresh token removed");
    }


    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUser> getAuthenticatedUser() {

        AuthenticatedUser response = authService.getAuthenticatedUser();
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
