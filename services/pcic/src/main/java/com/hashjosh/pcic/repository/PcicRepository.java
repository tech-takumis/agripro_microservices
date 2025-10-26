package com.hashjosh.pcic.repository;

import com.hashjosh.pcic.entity.Pcic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PcicRepository extends JpaRepository<Pcic, UUID> {
    boolean existsByEmail(String email);
    Optional<Pcic> findByUsername(String username);
    @Query("SELECT u FROM Pcic u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.id = :id")
    Optional<Pcic> findByIdWithRolesAndPermissions(@Param("id") UUID id);
    boolean existsByUsername(String username);
}
