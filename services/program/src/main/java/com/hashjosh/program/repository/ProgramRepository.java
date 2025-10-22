package com.hashjosh.program.repository;

import com.hashjosh.program.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProgramRepository extends JpaRepository<Program, UUID> {
}
