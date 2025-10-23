package com.example.agriculture.repository;

import com.example.agriculture.entity.Agriculture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgricultureRepository extends JpaRepository<Agriculture, UUID> {
    boolean existsByEmail(String email);

    @Query("SELECT a.username FROM Agriculture a WHERE a.username LIKE '100-%' ORDER BY a.username DESC LIMIT 1")
    String findLastUsername();

    Optional<Agriculture> findByUsernameContainingIgnoreCase(String username);
    @Query("SELECT u FROM Agriculture u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.id = :id")
    Optional<Agriculture> findByIdWithRolesAndPermissions(@Param("id") UUID id);

    @Query("SELECT a FROM Agriculture a WHERE " +
            "LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Agriculture> search(@Param("search") String search, Pageable pageable);

    Optional<Agriculture> findByUsername(String username);
}
