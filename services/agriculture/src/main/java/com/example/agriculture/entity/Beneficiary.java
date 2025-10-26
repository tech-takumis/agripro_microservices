package com.example.agriculture.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "beneficiaries")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    private boolean isActive;
    private LocalDateTime assignAt;

    @Override
    public String toString() {
        return "Beneficiary{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                '}';
    }
}
