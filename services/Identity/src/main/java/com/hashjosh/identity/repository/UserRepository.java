package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Set<User> findAllUsersByTenantNameIgnoreCase(String tenantName);

    Set<User> findAllUsersByTenantKeyIgnoreCase(String tenantKey);


    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.role", "permissions", "attributes"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithDetails(UUID id);

    @EntityGraph(attributePaths = {"roles", "roles.role", "permissions", "attributes"})
    @Query("SELECT u FROM User u")
    Set<User> findAllWithDetails();

    @EntityGraph(attributePaths = {"roles", "roles.role", "permissions", "attributes"})
    @Query("SELECT u FROM User u WHERE LOWER(u.tenant.key) = LOWER(:tenantKey)")
    Set<User> findAllUsersByTenantKeyIgnoreCaseWithDetails(String tenantKey);
}
