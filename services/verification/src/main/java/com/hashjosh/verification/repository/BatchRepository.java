package com.hashjosh.verification.repository;

import com.hashjosh.verification.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {
    // Returns all batches where current time is between startDate and endDate (available)
    List<Batch> findByStartDateBeforeAndEndDateAfter(LocalDateTime now, LocalDateTime now2);

    // More explicit: available means not expired and already started
    default List<Batch> findAllAvailableBatches() {
        LocalDateTime now = LocalDateTime.now();
        return findByStartDateBeforeAndEndDateAfter(now, now);
    }
}
