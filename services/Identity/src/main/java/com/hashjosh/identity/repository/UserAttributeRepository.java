package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAttributeRepository extends JpaRepository<UserAttribute, UUID> {
    List<UserAttribute> findByUserId(UUID userId);
}
