package com.example.agriculture.repository;

import com.example.agriculture.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, Long> {
}
