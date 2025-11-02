package com.example.agriculture.controller;

import com.example.agriculture.config.CustomUserDetails;
import com.example.agriculture.dto.auth.*;
import com.example.agriculture.dto.rbac.RoleResponse;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.service.AuthService;
import com.example.agriculture.service.InvitationService;
import com.example.agriculture.service.RefreshTokenService;
import com.example.agriculture.service.RoleService;
import com.hashjosh.jwtshareable.service.JwtService;
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
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agriculture/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RoleService roleService;
    private final InvitationService invitationService;


    @PostMapping("/invite")
    public ResponseEntity<String> inviteStaff(
            @RequestParam String email
    ){
        invitationService.sendInvitationEmail(email);
        return ResponseEntity.ok("Invitation sent to " + email);
    }

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> register(
            @RequestParam() String token,
            @RequestBody @Valid RegistrationRequest request
    ){
        invitationService.validateInvitationToken(token);
        Agriculture agriculture = authService.register(request);
        invitationService.markAsUsed(token);
        return ResponseEntity.ok(
                RegistrationResponse.builder()
                        .username(agriculture.getUsername())
                        .message("User Registered Successfully")
                        .error(null)
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

        // First authenticate and get tokens
        LoginResponse tokens = authService.login(request, httpRequest);

        // Set cookies
        Cookie accessCookie = buildCookie("ACCESS_TOKEN", tokens.getAccessToken(),
                (int) (jwtExpirySeconds(tokens.getAccessToken())));
        Cookie refreshCookie = buildCookie("REFRESH_TOKEN", tokens.getRefreshToken(),
                (int) Duration.ofDays(1).toSeconds());

        httpResponse.addCookie(accessCookie);
        httpResponse.addCookie(refreshCookie);

        AuthenticatedResponse response = AuthenticatedResponse.builder()
                .webSocketToken(tokens.getAccessToken())
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
    public ResponseEntity<AuthUser> getAuthenticatedUser() {
        CustomUserDetails userDetails = getUserDetails();

        // Get agriculture entity with roles
        Agriculture agriculture = authService.getAgricultureWithRoles(userDetails.getUserId());

        Set<RoleResponse> roles = agriculture.getRoles().stream()
                .map(roleService::getRole)
                .collect(Collectors.toSet());

        AuthUser response = AuthUser.builder()
                .id(agriculture.getId())
                .username(agriculture.getUsername())
                .email(agriculture.getEmail())
                .firstName(agriculture.getFirstName())
                .lastName(agriculture.getLastName())
                .phoneNumber(agriculture.getPhoneNumber())
                .roles(roles)
                .build();

        return ResponseEntity.ok(response);
    }

    private CustomUserDetails getUserDetails() {
        return (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }

    private Cookie buildCookie(String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setMaxAge(maxAgeSeconds);
        return cookie;
    }

    private long jwtExpirySeconds(String token) {
        Claims claims = jwtService.getAllClaims(token);
        return (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
    }
}
