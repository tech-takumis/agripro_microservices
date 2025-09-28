package com.hashjosh.users.controller;

import com.hashjosh.jwtshareable.service.JwtService;
import com.hashjosh.users.config.CustomUserDetails;
import com.hashjosh.users.dto.*;
import com.hashjosh.kafkacommon.user.TenantType;
import com.hashjosh.users.entity.User;
import com.hashjosh.users.exception.TenantIdException;
import com.hashjosh.users.services.RefreshTokenService;
import com.hashjosh.users.services.UserService;
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
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    private TenantType getTenantHeader(HttpServletRequest request) {
    String tenantHeader = request.getHeader("X-Tenant-ID");
    if (tenantHeader == null || tenantHeader.isBlank()) {
        throw new TenantIdException(
                "Missing X-Tenant-ID header",
                HttpStatus.NOT_FOUND.value()
        );
    }

    try {
        // Convert to uppercase to match enum values
        return TenantType.valueOf(tenantHeader.toUpperCase());
    } catch (IllegalArgumentException ex) {
        throw new TenantIdException(
                "Invalid tenant id. Allowed values are: " + 
                Arrays.toString(TenantType.values()),
                HttpStatus.BAD_REQUEST.value()
        );
    }
}

    @PostMapping("/staff/registration")
    public ResponseEntity<RegistrationResponse.StaffRegistrationResponse> registerStaff(
            @RequestBody RegistrationRequest.StaffRegistrationRequest request, HttpServletRequest httpRequest) {

        // Get tenant header and validate
        TenantType tenantType = getTenantHeader(httpRequest);
        request.setTenantId(tenantType);
        User user = userService.registerStaff(request);
        return ResponseEntity.ok(
                RegistrationResponse.StaffRegistrationResponse.builder()
                        .username(user.getUsername())
                        .message("Staff Registered Successfully")
                        .success(true)
                        .error(null)
                        .status(HttpStatus.CREATED.value())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    @PostMapping("/farmer/registration")
    public ResponseEntity<RegistrationResponse.FarmerRegistrationResponse> registerFarmer(
            @RequestBody RegistrationRequest.FarmerRegistrationRequest farmer,
            HttpServletRequest request
    ){

        // Get the tenant Id from the header
        TenantType tenantType = getTenantHeader(request);

        if(tenantType != TenantType.FARMER){
            return ResponseEntity.badRequest().body(
                    RegistrationResponse.FarmerRegistrationResponse.builder()
                            .username(null)
                            .message("Only farmers can register")
                            .error("Error non farmers can't register")
                            .success(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }

        farmer.setTenantId(tenantType);
        User user = userService.registerFarmer(farmer);

        return ResponseEntity.ok(
                RegistrationResponse.FarmerRegistrationResponse.builder()
                        .username(user.getUsername())
                        .message("Farmer Registered Successfully")
                        .error(null)
                        .success(true)
                        .status(HttpStatus.CREATED.value())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String tenantId = httpRequest.getHeader("X-Tenant-ID");
        if (tenantId == null || tenantId.isBlank()) {
            return ResponseEntity.badRequest().body("Missing X-Tenant-ID header");
        }

        String clientIp = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);

        LoginResponse tokens = userService.login(request, tenantId, clientIp, userAgent);

        // For PCIC or AGRICULTURE, set cookies
        if (tenantId.equalsIgnoreCase("pcic") || tenantId.equalsIgnoreCase("agriculture")) {
            Cookie accessCookie = buildCookie("ACCESS_TOKEN", tokens.getAccessToken(),
                    (int) (jwtExpirySeconds(tokens.getAccessToken())));
            Cookie refreshCookie = buildCookie("REFRESH_TOKEN", tokens.getRefreshToken(),
                    (int) Duration.ofDays(1).toSeconds());

            httpResponse.addCookie(accessCookie);
            httpResponse.addCookie(refreshCookie);

            return ResponseEntity.ok("Login successful");
        }

        // For FARMER, return tokens in body
        return ResponseEntity.ok(tokens);
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
        String tenantId = jwtService.getAllClaims(accessToken).get("tenantId", String.class);

        // ❌ Delete refresh token from DB (always do this)
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        // 🧹 Tenant-specific behavior
        if ("pcic".equalsIgnoreCase(tenantId) || "agriculture".equalsIgnoreCase(tenantId)) {
            // Expire cookies by setting Max-Age=0
            Cookie emptyAccessCookie = new Cookie("ACCESS_TOKEN", null);
            emptyAccessCookie.setPath("/");
            emptyAccessCookie.setHttpOnly(true);
            emptyAccessCookie.setMaxAge(0);
            response.addCookie(emptyAccessCookie);

            Cookie emptyRefreshCookie = new Cookie("REFRESH_TOKEN", null);
            emptyRefreshCookie.setPath("/");
            emptyRefreshCookie.setHttpOnly(true);
            emptyRefreshCookie.setMaxAge(0);
            response.addCookie(emptyRefreshCookie);

            return ResponseEntity.ok("Logged out: cookies cleared and refresh token removed");
        }

        if ("farmer".equalsIgnoreCase(tenantId)) {
            // For farmers: only remove refresh token from DB
            return ResponseEntity.ok("Logged out: refresh token removed");
        }

        // Default: handle unknown tenants gracefully
        return ResponseEntity.ok("Logged out");
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

        User user = customUserDetails.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        AuthenticatedResponse response = userService.getAuthenticatedUser(user);
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