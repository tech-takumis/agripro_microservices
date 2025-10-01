package com.hashjosh.farmer.repository;

import com.hashjosh.farmer.entity.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, UUID> {
}
