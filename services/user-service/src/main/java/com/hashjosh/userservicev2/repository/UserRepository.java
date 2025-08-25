package com.hashjosh.userservicev2.repository;

import com.hashjosh.userservicev2.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u where u.username = :username AND u.deleted = false ")
    Optional<User> findUserName(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deleted = false")
    Optional<User> existsByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deleted = false")
    Optional<User> existByIdAndNotDeleted(@Param("id") int id);

    @Query("SELECT u FROM User u where u.email = :email AND u.deleted = false")
    Optional<User> findEmail(@Param("email") String email);

    // Find all non-deleted User
    @Query("SELECT u FROM  User u where u.deleted = false ")
    List<User> findAllNotDeletedUser();

    // Find all deleted users
    @Query("SELECT u from User u where u.deleted = true")
    List<User> findAllDeletedUser();

    // Soft delete by id
    @Modifying
    @Query("UPDATE User u SET u.deleted = true, u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDelete(@Param("id") int id);

    // Restore user
    @Modifying
    @Query("UPDATE User u SET u.deleted = false, u.deletedAt = null WHERE u.id = :id")
    void restore(@Param("id") Long id);


}


