package com.hashjosh.application.repository;

import com.hashjosh.application.model.ApplicationField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationFieldsRepository extends JpaRepository<ApplicationField, UUID> {
}
