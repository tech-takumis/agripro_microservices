package com.hashjosh.pcic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

// 1. Entity
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
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
