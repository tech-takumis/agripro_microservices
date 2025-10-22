package com.hashjosh.communication.repository;

import com.hashjosh.communication.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        SELECT DISTINCT m FROM Message m
        LEFT JOIN FETCH m.attachments a
        WHERE m.senderId = :senderId
        ORDER BY m.createdAt ASC
        """)
    List<Message> findMessageBySenderIdWithAttachments(@Param("senderId") UUID senderId);

    @Query("""
        SELECT m FROM Message m
        LEFT JOIN FETCH m.attachments
        WHERE m.senderId = :receiverId OR m.receiverId = :senderId
        ORDER BY m.createdAt ASC
        """)
    List<Message> findMessageBySenderIdOrReceiverIdAndAttachments(@Param("senderId") UUID senderId);
}