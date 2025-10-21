package com.hashjosh.communication.repository;


import com.hashjosh.communication.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

@Query(
        """
SELECT m FROM Message m
JOIN Conversation c ON m.conversationId = c.id
WHERE c.type = 'FARMER_AGRICULTURE'
AND (c.senderId = :farmerId OR c.receiverId = :farmerId)
ORDER BY m.createdAt ASC
"""
)
    List<Message> findMessagesByFarmerIdAndConversationType(@Param("farmerId") UUID farmerId);
}
