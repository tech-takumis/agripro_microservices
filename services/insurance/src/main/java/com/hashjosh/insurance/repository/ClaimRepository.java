package com.hashjosh.insurance.repository;

import com.hashjosh.insurance.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
}
