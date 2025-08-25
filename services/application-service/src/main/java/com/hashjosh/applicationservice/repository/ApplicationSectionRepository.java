package com.hashjosh.applicationservice.repository;

import com.hashjosh.applicationservice.model.ApplicationSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationSectionRepository  extends JpaRepository<ApplicationSection, Long> {
}
