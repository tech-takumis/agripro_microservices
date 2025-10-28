package com.hashjosh.application.repository;

import com.hashjosh.application.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    @Query("""
    SELECT a FROM Application a
    WHERE a.type.id = :applicationTypeId
""")
    List<Application> findAllByApplicationTypeId(@Param("applicationTypeId") UUID applicationTypeId);




}
