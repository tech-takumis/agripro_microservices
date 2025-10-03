package com.example.agriculture.repository;

import com.example.agriculture.entity.Batch;
import com.example.agriculture.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {

    Optional<Batch> findByStatus(BatchStatus status);

    @Query("SELECT b FROM Batch b WHERE b.status = 'OPEN' AND b.startDate <= :currentDate AND b.endDate >= :currentDate")
    Optional<Batch> findOpenBatch(LocalDateTime currentDate);
}
