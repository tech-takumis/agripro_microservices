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
SELECT m from Message  m
JOIN User sender ON m.senderId = sender.id
JOIN User receiver ON m.receiverId = receiver.id
JOIN DesignatedStaff  ds ON receiver.id = ds.userId
WHERE sender.id = :farmerId
   AND sender.serviceType = 'FARMER'
   AND ds.serviceType = 'AGRICULTURE'
UNION 
SELECT m from Message  m
JOIN User  sender ON m.senderId = sender.id
JOIN User  receiver ON m.receiverId = receiver.id
JOIN DesignatedStaff  ds ON sender.id = ds.userId
WHERE receiver.id = :farmerId
    AND receiver.serviceType = 'FARMER'
    AND ds.serviceType = 'AGRICULTURE'
ORDER BY m.createdAt ASC
"""
)
    List<Message> findMessagesBetweenFarmerAndAgricultureStaff(@Param("farmerId") UUID farmerId);
}
