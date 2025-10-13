package com.hashjosh.communication.repository;

import com.hashjosh.communication.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.senderId = :senderId AND c.receiverId = :receiverId) OR " +
           "(c.senderId = :receiverId AND c.receiverId = :senderId)")
    Optional<Conversation> findBySenderIdAndReceiverId(
            @Param("senderId") UUID senderId,
            @Param("receiverId") UUID receiverId
    );
}
