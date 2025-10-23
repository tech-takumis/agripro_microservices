package com.example.agriculture.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agriculture {
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permission",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;


    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore
    private AgricultureProfile agricultureProfile;

    private boolean isActive;

    public Set<String> getEffectivePermissions() {
        Set<String> permissionFromRoles = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        Set<String> directPermissions = permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        permissionFromRoles.addAll(directPermissions);

        return permissionFromRoles;
    }

    // Method to assign direct permission with a check to avoid duplicates
    public void assignDirectPermission(Permission permission){
        Set<String> effectivePermissions = getEffectivePermissions();
        if(effectivePermissions.contains(permission.getName())){
            throw  new IllegalArgumentException("Permission "+permission.getName()+" already assigned directly or via role");
        }

       permissions.add(permission);
    }
}