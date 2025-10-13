package com.example.agriculture.repository;

import com.example.agriculture.entity.Beneficiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {
    @Query(
            """
        SELECT b FROM Beneficiary b WHERE 
        b.userId = :userId AND
        b.program.id = :programId
            """
    )
    Optional<Beneficiary> findByUserIdAndProgramId(@Param("userId") UUID userId,@Param("programId") UUID programId);

    @Query("SELECT b FROM Beneficiary b WHERE b.program.id = :programId")
    Page<Beneficiary> findByProgramId(@Param("programId") UUID programId, Pageable pageable);
}
