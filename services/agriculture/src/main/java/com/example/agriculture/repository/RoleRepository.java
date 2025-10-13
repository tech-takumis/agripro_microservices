package com.example.agriculture.repository;

import com.example.agriculture.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions")
    List<Role> findAllWithPermissions();
    Optional<Role> findByName(String name);


    @Query(
            """
        SELECT  r FROM Role r
        LEFT JOIN FETCH r.permissions
        WHERE r.id = :id
        """
    )
    Optional<Role> findByIdWithPermissions(UUID id);
}
