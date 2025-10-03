package com.hashjosh.pcic.entity;

import com.hashjosh.pcic.enums.PolicyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "policies")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "submission_id", nullable = false)
    private UUID submissionId; // Maps to Application.id

    @Column(name = "policy_number", length = 50, unique = true)
    private String policyNumber;

    @Column(name = "coverage_amount")
    private Double coverageAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PolicyStatus status; // PENDING, APPROVED, REJECTED, ACTIVE, EXPIRED, CANCELLED

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
