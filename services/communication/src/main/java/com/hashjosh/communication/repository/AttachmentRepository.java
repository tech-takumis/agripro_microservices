package com.hashjosh.communication.repository;

import com.hashjosh.communication.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    Optional<Attachment> findByDocumentId(UUID documentId);
}
