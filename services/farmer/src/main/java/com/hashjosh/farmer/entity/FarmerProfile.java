package com.hashjosh.farmer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Government IDs often used: RSBSA (registry), SSS/Pag-IBIG optional, TIN optional
    @Column(unique = true, nullable = false, name = "rsbsa_id")
    private String rsbsaId;        // e.g., RSBSA number used in many DA records
    @Column(unique = true, name = "national_id")
    private String nationalId;     // e.g., PhilID, SSs, TIN (optional)

    // Basic personal info
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "gender")
    private String gender;
    @Column(name = "civil_status")
    private String civilStatus;

    @Column(name = "house_no")
    private String houseNo;
    @Column(name = "street")
    private String street;
    @Column(name = "barangay")
    private String barangay;
    @Column(name = "municipality")
    private String municipality;
    @Column(name = "province")
    private String province;
    @Column(name = "region")
    private String region;

    // Farming specifics
    @Column(name = "farmer_type")
    private String farmerType;     // e.g., "smallholder", "commercial", "tenant", "owner-operator"
    @Column(name = "primary_occupation")
    private String primaryOccupation; // often "farmer" or "farmer-fisher"
    @Column(name = "total_farm_area_ha")
    private Double totalFarmAreaHa; // total farm area in hectares

    // Tenure and land ownership
    @Column(name = "land_tenure")
    private String landTenure;     // e.g., "owner", "tenant", "sharecropper", "informal"

    // PCIC / Insurance related
    @Column(name = "pcic_enrolled")
    private boolean pcicEnrolled;
    @Column(name = "pcic_policy_number")
    private String pcicPolicyNumber;
    @Column(name = "pcic_policy_start")
    private LocalDateTime pcicPolicyStart;
    @Column(name = "pcic_policy_end")
    private LocalDateTime pcicPolicyEnd;

    // Socio-economic metadata
    @Column(name = "household_size")
    private Integer householdSize;
    @Column(name = "education_level")
    private String educationLevel;
    @Column(name = "annual_farm_income")
    private Double annualFarmIncome;


    // Audit / metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @OneToOne(mappedBy = "farmerProfile",cascade = CascadeType.PERSIST)
    @ToString.Exclude
    @JsonIgnore
    private Farmer farmer;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}