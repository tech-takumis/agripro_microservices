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
    WHERE a.batch.applicationType.provider.id = :providerId
    """)
    List<Application> findByProviderId(UUID providerId);


    /*
    *Goal: For a given batch (e.g., batch 10) and application type (e.g., type 123), get all Application in that batch with that type.
     Assuming:
   Application has a Batch (batch field) and Batch has an ApplicationType (applicationType field).
   Query:
    *
    * /
    */
    @Query("""
    SELECT a FROM Application a
    WHERE a.batch.id = :batchId
    AND a.batch.applicationType.id = :applicationTypeId
    """)
    List<Application> findAllByBatchIdAndApplicationTypeId(@Param("batchId") UUID batchId, UUID applicationTypeId);


    @Query("""
    SELECT a FROM Application a
    WHERE LOWER(a.batch.name) = LOWER(:batchName)
    AND a.batch.applicationType.id = :applicationTypeId
    """)
    List<Application> findAllByBatchNameAndApplicationTypeId(@Param("batchName") String batchName, UUID applicationTypeId);

    @Query("""
    SELECT a FROM Application a
    WHERE a.batch.applicationType.id = :applicationTypeId
""")
    List<Application> findAllByApplicationTypeId(@Param("applicationTypeId") UUID applicationTypeId);




}
