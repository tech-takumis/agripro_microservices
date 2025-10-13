package com.hashjosh.communication.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    private UUID conversationId;

    private UUID senderId;

    private UUID receiverId;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private Set<Attachment> attachments = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Helper method to add attachment
    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.getMessages().add(this);
    }

    // Helper method to remove attachment
    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.getMessages().remove(this);
    }
}
