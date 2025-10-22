package com.hashjosh.identity.entity;

import com.hashjosh.identity.enums.DesignatedType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "designated")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Designated {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID tenantId;
    private UUID userId;
    private UUID assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,  name = "type", length = 100)
    private DesignatedType type;

    private LocalDateTime assignedAt;
}
