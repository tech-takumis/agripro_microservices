package com.hashjosh.farmer.repository;

import com.hashjosh.farmer.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FarmerRepository extends JpaRepository<Farmer, UUID> {
    boolean existsByEmail(String email);
    Optional<Farmer> findByUsername(String username);
    @Query("SELECT u FROM Farmer u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.id = :id")
    Optional<Farmer> findByIdWithRolesAndPermissions(@Param("id") UUID id);
    boolean existsByUsername(String username);
}
