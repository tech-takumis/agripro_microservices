package com.hashjosh.pcic.entity;

import com.hashjosh.pcic.enums.ClaimStatus;
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

    @Column(name = "submission_id", nullable = false)
    private UUID submissionId; // Maps to Application.id

    @Column(name = "claim_amount", nullable = false)
    private Double claimAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", length = 50)
    private ClaimStatus payoutStatus; // PENDING, APPROVED, REJECTED, PAID

    @CreationTimestamp
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

}
