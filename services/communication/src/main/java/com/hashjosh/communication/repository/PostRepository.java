package com.hashjosh.communication.repository;

import com.hashjosh.communication.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByOrderByIdDesc(Pageable pageable);

    List<Post> findByAuthorId(UUID authorId);

    List<Post> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable pageable);


}
