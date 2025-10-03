package com.hashjosh.application.model;


import com.hashjosh.application.enums.BatchStatus;
import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "batches")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name; // e.g., "2025-2026 A"

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // e.g., Feb 1

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // e.g., April 30

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private BatchStatus status; // OPEN, CLOSED, VERIFIED

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
