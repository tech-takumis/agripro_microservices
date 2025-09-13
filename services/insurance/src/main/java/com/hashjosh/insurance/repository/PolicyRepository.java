package com.hashjosh.insurance.repository;

import com.hashjosh.insurance.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {
}
