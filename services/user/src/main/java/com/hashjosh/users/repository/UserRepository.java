package com.hashjosh.users.repository;

import com.hashjosh.users.entity.User;
import com.hashjosh.users.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByUserType(UserType userType);
    Optional<User> findByUsername(String username);

}

