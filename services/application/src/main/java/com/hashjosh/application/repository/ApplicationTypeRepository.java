package com.hashjosh.application.repository;

import com.hashjosh.application.model.ApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationTypeRepository extends JpaRepository<ApplicationType, UUID> {
}
