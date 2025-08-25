package com.hashjosh.applicationservice.repository;

import com.hashjosh.applicationservice.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
