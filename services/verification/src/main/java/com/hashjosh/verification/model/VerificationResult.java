package com.hashjosh.verification.model;

import com.hashjosh.verification.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_results")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "event_id")
    private UUID eventId;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId; // FK to Application

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 50)
    private VerificationStatus status; // APPROVED, REJECTED, PENDING

    @Column(name = "approved_by_adjuster")
    private Boolean approvedByAdjuster;

    @Column(name = "is_verified_by_underwriter")
    private Boolean verifiedByUnderwriter;

    @Column(name = "inspection_type", length = 50)
    private String inspectionType; // e.g., FIELD_VISIT, REMOTE

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @Column(name = "report", length = 255)
    private String report;

    @CreationTimestamp
    private LocalDateTime verifiedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
