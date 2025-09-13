package com.hashjosh.users.entity;

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
    @Column(columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private String userRef;

    @Column(nullable = false)
    private String clientIp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userAgent;

    @Column(nullable = false)
    private Instant expiry;

    @CreationTimestamp
    private Instant createdAt;
}
