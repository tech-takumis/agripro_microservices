package com.hashjosh.rsbsa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RsbsaRepository extends JpaRepository<Rsbsa, UUID> {

    Optional<Rsbsa> findByRsbsaIdEqualsIgnoreCase(String rsbsaId);
}
