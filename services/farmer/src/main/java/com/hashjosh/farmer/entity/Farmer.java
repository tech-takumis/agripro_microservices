package com.hashjosh.farmer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Farmer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true,name = "username")
    private String username;

    @Column(nullable = false,name = "password")
    private String password;

    @Column(nullable = false,name = "first_name")
    private String firstName;

    @Column(nullable = false,name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true,name = "email")
    private String email;

    @Column(nullable = true, name = "phone_number")
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private FarmerProfile farmerProfile;
}