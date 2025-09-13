package com.hashjosh.workflow.model;

import com.hashjosh.workflow.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_status_history")
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WorkflowStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId; // Store the event ID as a unique reference

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ApplicationStatus status;
    // e.g. SUBMITTED, VERIFIED, REJECTED, POLICY_ISSUED, CLAIM_APPROVED

    @Column(name = "comments", length = 255)
    private String comments;

    @Column(name = "updated_by")
    private UUID updatedBy; // userId of actor

    @CreationTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public  WorkflowStatusHistory(UUID applicationId, ApplicationStatus status,  UUID updatedBy, Long version) {
        this.applicationId = applicationId;
        this.status = status;
        this.updatedBy = updatedBy;
        this.version = version;
    }

}
