package com.example.agriculture.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "programs")
@Getter
@Setter
@ToString(exclude = "beneficiaries")
@EqualsAndHashCode(exclude = "beneficiaries")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private float budget;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Beneficiary> beneficiaries;

    private int completedPercentage;
    private String status; // planned, ongoing, completed

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public void addBeneficiary(Beneficiary beneficiary) {
        if (beneficiaries == null) {
            beneficiaries = new ArrayList<>();
        }
        beneficiaries.add(beneficiary);
        beneficiary.setProgram(this);
    }

    public void removeBeneficiary(Beneficiary beneficiary) {
        if (beneficiaries != null) {
            beneficiaries.remove(beneficiary);
            beneficiary.setProgram(null);
        }
    }
}
