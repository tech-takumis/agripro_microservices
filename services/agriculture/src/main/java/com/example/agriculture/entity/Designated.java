package com.example.agriculture.entity;

import com.example.agriculture.enums.DesignatedType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "designated")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Designated {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Agriculture userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designated_by")
    private Agriculture designatedBy;

    @Enumerated(EnumType.STRING)
    private DesignatedType type;

    private boolean isActive;
    private LocalDateTime assignAt;
}
