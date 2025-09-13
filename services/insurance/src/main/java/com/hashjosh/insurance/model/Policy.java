package com.hashjosh.insurance.model;

import com.hashjosh.insurance.enums.PolicyStatus;
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

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "policy_number", length = 50, unique = true)
    private String policyNumber;

    @Column(name = "coverage_amount")
    private Double coverageAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PolicyStatus status; // ACTIVE, EXPIRED, CANCELLED

    @CreationTimestamp
    private LocalDateTime issuedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
