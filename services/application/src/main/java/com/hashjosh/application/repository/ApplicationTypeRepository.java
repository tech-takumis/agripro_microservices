package com.hashjosh.application.repository;

import com.hashjosh.application.model.ApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationTypeRepository extends JpaRepository<ApplicationType, UUID> {
    List<ApplicationType> findAllByProvider_Name(String providerName);

    Optional<ApplicationType> findByProvider_Name(String name);

    @Query("SELECT b.isAvailable FROM ApplicationType a JOIN Batch  b WHERE a.id = :id AND b.applicationType.id = a.id")
    Boolean isAvailable(@Param("id") UUID id);

    List<ApplicationType> findByProvider_Id(UUID providerId);
}
