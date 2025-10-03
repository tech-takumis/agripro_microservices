package com.hashjosh.pcic.repository;

import com.hashjosh.pcic.entity.ValidationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ValidationRecordRepository extends JpaRepository<ValidationRecord, UUID> {
}
