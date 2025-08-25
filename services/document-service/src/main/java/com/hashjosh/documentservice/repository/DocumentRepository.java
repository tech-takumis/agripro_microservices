package com.hashjosh.documentservice.repository;

import com.hashjosh.documentservice.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
