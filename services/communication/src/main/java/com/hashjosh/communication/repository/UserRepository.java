package com.hashjosh.communication.repository;

import com.hashjosh.communication.entity.User;
import com.hashjosh.constant.communication.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByUserId(UUID userId);

    Optional<User> findByUserId(UUID userId);

    List<User> findByServiceType(ServiceType serviceType);
}
