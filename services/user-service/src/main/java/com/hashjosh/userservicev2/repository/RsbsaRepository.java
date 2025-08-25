package com.hashjosh.userservicev2.repository;

import com.hashjosh.userservicev2.models.Rsbsa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RsbsaRepository extends JpaRepository<Rsbsa, Long> {

    Optional<Rsbsa> findByRsbsaIdEqualsIgnoreCase(String rsbsaId);
}
