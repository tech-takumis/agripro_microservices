package com.hashjosh.verification.entity;

import com.hashjosh.constant.verification.VerificationStatus;
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

    @Column(name = "submission_id", nullable = false)
    private UUID submissionId; // Maps to Application.id

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @Column(name = "verification_type", length = 50)
    private String verificationType; // e.g., "Application Verification"

    @Column(name = "is_forwarded")
    private boolean isForwarded;

    @Column(name = "report", length = 255)
    private String report;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private VerificationStatus status; // PENDING, COMPLETED, REJECTED

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
