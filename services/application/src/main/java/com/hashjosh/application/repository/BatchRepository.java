package com.hashjosh.application.repository;

import com.hashjosh.application.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {

    Optional<Batch> findByApplicationTypeId(UUID applicationTypeId);


    List<Batch> findAllByIsAvailableAndApplicationType_Id(boolean isAvailable, UUID applicationTypeId);
    Optional<Batch> findByName(String name);
}
