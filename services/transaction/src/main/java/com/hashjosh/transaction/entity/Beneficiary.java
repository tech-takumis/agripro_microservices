package com.hashjosh.transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String type; // e.g., Farmer, Cooperative, NGO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Override
    public String toString() {
        return "Beneficiary{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                '}';
    }
}
