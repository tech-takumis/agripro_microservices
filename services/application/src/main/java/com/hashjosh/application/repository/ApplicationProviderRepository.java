package com.hashjosh.application.repository;

import com.hashjosh.application.model.ApplicationProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationProviderRepository extends JpaRepository<ApplicationProvider, UUID> {
    Optional<ApplicationProvider> findByName(String name);

    boolean existsByName(String name);
}
