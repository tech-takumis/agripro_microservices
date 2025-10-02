package com.hashjosh.farmer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

// 1. Entity
@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(columnDefinition = "TEXT", name = "token")
    private String token;

    @Column(nullable = false,name = "user_ref")
    private String userRef;

    @Column(nullable = false, name = "client_ip")
    private String clientIp;

    @Column(nullable = false, columnDefinition = "TEXT",name = "user_agent")
    private String userAgent;

    @Column(nullable = false,name = "expiry")
    private Instant expiry;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
}
