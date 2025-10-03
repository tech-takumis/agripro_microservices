package com.example.agriculture.entity;


import com.example.agriculture.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_records")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @Column(name = "verification_type", length = 50)
    private String verificationType; // e.g., "Batch Verification"

    @Column(name = "report", length = 255)
    private String report;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private VerificationStatus status; // PENDING, COMPLETED, REJECTED

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
