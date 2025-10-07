package com.hashjosh.document.repository;

import com.hashjosh.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByUploadedBy(UUID userId);
}
