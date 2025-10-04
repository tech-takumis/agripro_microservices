package com.hashjosh.pcic.repository;

import com.hashjosh.pcic.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
}
