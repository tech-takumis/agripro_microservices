package com.hashjosh.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;
    private String email;
    private String password;

    private String firstName;
    private String lastName;

    private boolean emailVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private boolean active = true;
    private boolean deleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAttribute> attributes = new HashSet<>();

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
