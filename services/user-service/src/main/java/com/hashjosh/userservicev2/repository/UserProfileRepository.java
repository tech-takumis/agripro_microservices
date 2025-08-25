package com.hashjosh.userservicev2.repository;

import com.hashjosh.userservicev2.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
