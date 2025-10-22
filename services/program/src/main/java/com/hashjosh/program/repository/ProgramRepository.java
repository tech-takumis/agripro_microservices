package com.hashjosh.transaction.repository;

import com.hashjosh.transaction.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProgramRepository extends JpaRepository<Program, UUID> {
}
