package com.example.agriculture.entity;

import com.hashjosh.constant.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_results")
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

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @Column(name = "inspection_type", length = 50)
    private String inspectionType;

    @Column(name = "report", length = 255)
    private String report;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
