package com.hashjosh.insurance.entity;

import com.hashjosh.constant.pcic.enums.InsuranceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "insurances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID submissionId;

    private UUID submittedBy;

    @Enumerated(EnumType.STRING)
    private InsuranceStatus status;

    @OneToOne(mappedBy = "insurance")
    private InspectionRecord inspectionRecord;

    @OneToOne(mappedBy = "insurance")
    private Policy policy;

    @OneToOne(mappedBy = "insurance")
    private Claim claim;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
