package com.hashjosh.pcic.entity;


import com.hashjosh.constant.pcic.enums.InspectionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inspection_records")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "submission_id", nullable = false)
    private UUID submissionId; // Maps to Application.id

    @Column(name = "submitted_by")
    private UUID submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private InspectionStatus status; // PENDING, COMPLETED, INVALID

    @Column(name = "comments", length = 255)
    private String comments; // Inspection/validation notes

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
