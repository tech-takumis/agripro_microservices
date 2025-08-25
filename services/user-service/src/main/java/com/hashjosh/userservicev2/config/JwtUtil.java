package com.hashjosh.userservicev2.config;

import com.hashjosh.userservicev2.dto.JwtProperties;
import com.hashjosh.userservicev2.models.Authority;
import com.hashjosh.userservicev2.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;


    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }


    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        Date expiryDate = Date.from(Instant.now().plusMillis(Long.parseLong(jwtProperties.expiration())));


       claims.put("role", user.getRole().getName());
       claims.put("permissions", user.getRole().getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()));
       claims.put("userId", user.getId());

        System.out.println("Claim role in JwtUtl class:::: " + user.getRole());
        System.out.println("Claim permissions in JwtUtl class:::: " + user.getRole().getAuthorities());
        System.out.printf("Jwt secret key ::: %s%n", jwtProperties.secret());

        System.out.println("Jwt expiration:: " + jwtProperties.expiration());

        return  Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .claims(claims)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
