package com.hashjosh.userservicev2.controller;


import com.hashjosh.userservicev2.dto.FarmerRequestDto;
import com.hashjosh.userservicev2.dto.LoginRequestDTO;
import com.hashjosh.userservicev2.dto.UserRequestDto;
import com.hashjosh.userservicev2.models.Role;
import com.hashjosh.userservicev2.models.User;
import com.hashjosh.userservicev2.services.AuthService;
import com.hashjosh.userservicev2.services.UserEmailService;
import com.hashjosh.userservicev2.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService;
    private final UserEmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequest,
                                   HttpServletRequest request, HttpServletResponse response) {
       try{
           AuthService.LoginResult result = authService.login(
                   loginRequest.username(),
                   loginRequest.password(),
                   loginRequest.rememberMe()
           );

           boolean isWeb = request.getHeader("User-Agent") != null
                   && request.getHeader("User-Agent").contains("Mozilla");

           if(isWeb){
               Cookie jwtCookie = new Cookie("jwt", result.jwt());
               jwtCookie.setHttpOnly(true);
               jwtCookie.setSecure(false);
               jwtCookie.setPath("/");
               jwtCookie.setMaxAge(result.expiry());
               jwtCookie.setAttribute("SameSite", "Lax");
               response.addCookie(jwtCookie);

               return ResponseEntity.ok(Map.of("message", "Login successful"));
           }else {
               // Mobile: Return JWT in response body
               return ResponseEntity.ok(Map.of(
                       "message", "Login successful",
                       "jwt", result.jwt()
               ));
           }


       }catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Invalid username or password")
            );
       }catch (IllegalArgumentException e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                   Map.of("message", e.getMessage())
           );
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                   Map.of("message", "Error occurred during login")
           );
       }
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerStaff(
            @RequestBody @Valid UserRequestDto dto
    ) throws MessagingException {

        Role userRole = userService.getUserRole(dto.roleId());

        User user = userService.register(dto);

        // Todo: Send and event instead for email notification
        if("staff".equalsIgnoreCase(userRole.getName())){
            emailService.sendStaffRegistrationMail(user);
        }else {
            emailService.sendFarmerRegistrationMail(user);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registered successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String userAgent = request.getHeader("User-Agent");
        boolean isWeb = userAgent != null && userAgent.contains("Mozilla");

        if (!isWeb) {
            return ResponseEntity.ok(Map.of("message", "Logged out successfully (mobile)"));
        } else {
            Cookie jwtCookie = new Cookie("jwt", "");
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            jwtCookie.setAttribute("SameSite", "Lax");
            response.addCookie(jwtCookie);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
    }

}
