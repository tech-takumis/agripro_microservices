package com.hashjosh.insurance.entity;


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

    @OneToOne
    @JoinColumn(name = "insurance_id")
    private Insurance insurance;

    @Version
    private Long version;
}
