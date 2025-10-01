package com.example.agriculture.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgricultureProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "official_name")
    private String officialName = "Department of Agriculture";
    @Column(name = "acronym")
    private String acronym = "DA";
    @Column(name = "mandate")
    private String mandate; // statutory mandate summary
    @Column(name = "vision")
    private String vision;
    @Column(name = "mission")
    private String mission;
    @Column(name = "headquarters_address")
    private String headquartersAddress; // Elliptical Road, Diliman, Quezon City (HQ)
    @Column(name = "website")
    private String website; // https://www.da.gov.ph


    // Address
    @Column(name = "street")
    private String street;
    @Column(name = "barangay")
    private String barangay;
    @Column(name = "city")
    private String city;
    @Column(name = "province")
    private String province;
    @Column(name = "region")
    private String region;
    @Column(name = "country")
    private String country;
    @Column(name = "postal_code")
    private String postalCode;

    // Organizational structure highlights (useful if you model sub-agencies)
    @ElementCollection
    @CollectionTable(name = "attached_agencies", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "attached_agency")
    private List<String> attachedAgencies; // e.g., "PCC", "PHilMech", "Bureau of Agricultural Research"
    @ElementCollection
    @CollectionTable(name = "bureaus", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "bureaus")
    private List<String> bureaus;         // e.g., "Bureau of Animal Industry", "Bureau of Plant Industry"

    // Programs & services
    @ElementCollection
    @CollectionTable(name = "priority_programs", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "priority_programs")
    private List<String> priorityPrograms; // e.g., "palay production", "fisheries support", "postharvest mech."

    // Contacts / communications
    @Column(name = "public_affairs_email")
    private String publicAffairsEmail;

    // Governance & policy references
    @ElementCollection
    @CollectionTable(name = "policies", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "policy")
    private List<String> keyPolicies; // links or short descriptions (e.g., RSBSA, subsidies)

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @OneToOne(mappedBy = "agricultureProfile",cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Agriculture agriculture;

}
