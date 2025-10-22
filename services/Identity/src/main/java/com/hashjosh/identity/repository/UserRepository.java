package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Set<User> findAllUsersByTenantKeyIgnoreCase(String tenantKey);

    Boolean existsByTenantKeyIgnoreCaseAndId(String tenantKey, UUID id);
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

}
