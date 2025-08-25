package com.hashjosh.applicationservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "dynamic_fields", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private JsonNode dynamicFields;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "application_type_id")
    private ApplicationType applicationType;

    @Column(name = "userID", length = 50, nullable = false)
    private Long userId;

    @Column(name = "approved_by_adjuster")
    private Boolean approvedByAdjuster;

    @Column(name = "is_verified_by_underwriter")
    private Boolean isVerifiedByUnderwriter;

    @Column(name = "inspection_type", length = 50)
    private String inspectionType;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @Column(name = "policy_number", length = 50)
    private String policyNumber;

    @Column(name = "claim_amount")
    private Double claimAmount;

    @Column(name = "payout_status", length = 50)
    private String payoutStatus;


    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

}