package com.hashjosh.userservicev2.services;


import com.hashjosh.userservicev2.config.CustomUserDetails;
import com.hashjosh.userservicev2.config.JwtUtil;
import com.hashjosh.userservicev2.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResult login(String username, String password, boolean rememberMe) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password
                )
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.user();


        int expiry = rememberMe ? 60 * 60 * 24 *30 : -1;

        return new LoginResult(generateToken(user),expiry);
    }

    public String generateToken(User user) {
        return jwtUtil.generateToken(user);
    }

    public record LoginResult(String jwt, int expiry) {

    }
}
