package com.example.agriculture.repository;

import com.example.agriculture.entity.Designated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DesignatedRepository extends JpaRepository<Designated, UUID> {
    Optional<Designated> findByUserIdUsernameContainingIgnoreCase(String username);
}
