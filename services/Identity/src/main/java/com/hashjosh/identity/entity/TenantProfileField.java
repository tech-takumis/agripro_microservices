package com.hashjosh.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tenant_profile_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantProfileField {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String fieldKey; // e.g. "education", "farm_size"

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataType dataType;

    private boolean required;

    public enum DataType {
        TEXT, NUMBER, BOOLEAN, DATE, FILE
    }
}
