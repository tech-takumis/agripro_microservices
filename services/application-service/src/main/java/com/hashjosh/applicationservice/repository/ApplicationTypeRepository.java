package com.hashjosh.applicationservice.repository;

import com.hashjosh.applicationservice.model.ApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationTypeRepository extends JpaRepository<ApplicationType, Long> {
}
