package com.hashjosh.insurance.entity;

import com.hashjosh.constant.pcic.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "policy_id", nullable = false)
    private UUID policyId;

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

    @OneToOne
    @JoinColumn(name = "insurance_id")
    private Insurance insurance;

    @Version
    private Long version;

}
