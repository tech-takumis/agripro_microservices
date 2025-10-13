package com.example.agriculture.repository;

import com.example.agriculture.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findByType(String type, Pageable pageable);
    Page<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
