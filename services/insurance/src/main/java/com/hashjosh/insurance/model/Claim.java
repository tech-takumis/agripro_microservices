package com.hashjosh.insurance.model;

import com.hashjosh.insurance.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
@Getter
@Setter
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "policy_id", nullable = false)
    private UUID policyId;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "claim_amount", nullable = false)
    private Double claimAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", length = 50)
    private ClaimStatus payoutStatus; // PENDING, APPROVED, REJECTED, PAID

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
