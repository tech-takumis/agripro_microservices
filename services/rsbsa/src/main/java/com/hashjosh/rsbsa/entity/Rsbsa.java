package com.hashjosh.rsbsa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "rsbsa_records")
public class Rsbsa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String rsbsaId;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String gender;
    private String civilStatus;
    private String address;
    private String barangay;
    private String municipality;
    private String province;
    private String contactNumber;
    private String email;

    private String farmingType;
    private String primaryCrop;
    private String secondaryCrop;
    private Float farmArea;
    private String farmLocation;
    private String tenureStatus;

    private String sourceOfIncome;
    private float estimatedIncome;
    private Integer householdSize;
    private String educationLevel;
    private boolean withDisability;

}
