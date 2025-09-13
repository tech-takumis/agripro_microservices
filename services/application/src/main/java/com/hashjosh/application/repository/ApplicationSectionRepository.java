package com.hashjosh.application.repository;

import com.hashjosh.application.model.ApplicationSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationSectionRepository  extends JpaRepository<ApplicationSection, UUID> {
}
