package com.hashjosh.identity.repository;

import com.hashjosh.identity.entity.Designated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DesignatedRepository extends JpaRepository<Designated, UUID> {
}
