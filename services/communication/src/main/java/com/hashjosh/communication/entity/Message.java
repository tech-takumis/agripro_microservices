package com.hashjosh.communication.entity;


import com.hashjosh.constant.communication.enums.ConversationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private UUID senderId;

    private UUID receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "conversation_type")
    private ConversationType type;

    @Column(columnDefinition = "TEXT")
    private String text;

    @OneToMany(mappedBy = "message")
    private List<Attachment> attachments = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;


    public List<Attachment> getAttachments() {
        // Ensure never returns null
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        return attachments;
    }
}
