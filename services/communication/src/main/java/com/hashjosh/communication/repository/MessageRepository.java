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
SELECT DISTINCT m FROM Message m
JOIN User s ON m.senderId = s.id
JOIN User r ON m.receiverId = r.id
JOIN Conversation c ON m.conversationId = c.id
WHERE (
    (s.id = :farmerId AND s.serviceType = 'FARMER' AND r.serviceType = 'AGRICULTURE')
    OR 
    (r.id = :farmerId AND r.serviceType = 'FARMER' AND s.serviceType = 'AGRICULTURE')
)
ORDER BY m.createdAt ASC
"""
)
    List<Message> findMessagesBetweenFarmerAndAgricultureStaff(@Param("farmerId") UUID farmerId);

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
