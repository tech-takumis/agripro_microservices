package com.hashjosh.communication.repository;

import com.hashjosh.communication.entity.DesignatedStaff;
import com.hashjosh.constant.communication.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DesignatedStaffRepository extends JpaRepository<DesignatedStaff,String> {

    Optional<DesignatedStaff> findByServiceType(ServiceType serviceType);
}
