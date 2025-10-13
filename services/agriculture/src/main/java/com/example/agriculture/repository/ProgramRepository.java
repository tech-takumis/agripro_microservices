package com.example.agriculture.repository;

import com.example.agriculture.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    int countByStatus(String active);
}
