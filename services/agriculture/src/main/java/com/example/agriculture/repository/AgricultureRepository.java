package com.example.agriculture.repository;

import com.example.agriculture.entity.Agriculture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AgricultureRepository extends JpaRepository<Agriculture, UUID> {
    boolean existsByEmail(String email);
    Optional<Agriculture> findByUsername(String username);
    @Query("SELECT u FROM Agriculture u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.id = :id")
    Optional<Agriculture> findByIdWithRolesAndPermissions(@Param("id") UUID id);
    boolean existsByUsername(String username);
}
