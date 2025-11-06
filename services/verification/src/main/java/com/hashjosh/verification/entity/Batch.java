package com.hashjosh.verification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "batches")
@AllArgsConstructor
@NoArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "application_type_id", nullable = false)
    private UUID applicationTypeId;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "total_applications", columnDefinition = "int default 0")
    private int totalApplications;

    private String description;

    private boolean isAvailable;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(name = "max_applications", columnDefinition = "int default 10")
    private int maxApplications;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "batch", fetch = FetchType.EAGER)
    private List<VerificationRecord> verificationRecords;
}
