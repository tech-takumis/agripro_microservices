package com.hashjosh.pcic.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profile")
@Data
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "official_name")
    private String officialName = "Philippine Crop Insurance Corporation";
    @Column(name = "acronym")
    private String acronym = "PCIC";
    @Column(name = "mandate")
    private String mandate; // short text about mandate
    @Column(name = "vision")
    private String vision;
    @Column(name = "mission")
    private String mission;
    @Column(name = "core_values")
    private String coreValues;

    // Contact / corporate details (pulled from PCIC site)
    @Column(name = "head_office_address")
    private String headOfficeAddress;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "website")
    private String website; // e.g., https://pcic.gov.ph

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToOne(mappedBy = "userProfile")
    private User user;

}
