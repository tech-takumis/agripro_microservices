package com.hashjosh.pcic.entity;

import com.hashjosh.pcic.enums.ValidationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "validation_records")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "submission_id", nullable = false)
    private UUID submissionId; // Maps to Application.id

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private ValidationStatus status; // PENDING, VALIDATED, INVALID

    @Column(name = "comments", length = 255)
    private String comments; // Validation notes or rejection reason

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
