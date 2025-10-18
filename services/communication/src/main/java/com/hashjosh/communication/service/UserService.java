package com.hashjosh.communication.service;

import com.hashjosh.communication.dto.DesignatedResponse;
import com.hashjosh.communication.dto.FarmerResponseDto;
import com.hashjosh.communication.mapper.DesignatedMapper;
import com.hashjosh.communication.mapper.UserMapper;
import com.hashjosh.communication.repository.DesignatedStaffRepository;
import com.hashjosh.communication.repository.UserRepository;
import com.hashjosh.constant.communication.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final DesignatedStaffRepository designatedStaffRepository;
    private final UserRepository userRepository;
    private final DesignatedMapper designatedMapper;
    private final UserMapper userMapper;

    public DesignatedResponse findAgricultureDesignatedStaff() {
        log.info("Finding agriculture designated staff");
        return designatedStaffRepository.findByServiceType(ServiceType.AGRICULTURE)
                .map(designatedMapper::toDesignatedResponseDto)
                .orElseThrow(() -> new RuntimeException("No designated staff found for agriculture service type"));
    }

    public DesignatedResponse findPCICDesignatedStaff() {
        log.info("Finding PCIC designated staff");
        return designatedStaffRepository.findByServiceType(ServiceType.PCIC)
                .map(designatedMapper::toDesignatedResponseDto)
                .orElseThrow(() -> new RuntimeException("No designated staff found for PCIC service type"));
    }

    public List<FarmerResponseDto> getAllFarmers() {
        return userRepository.findByServiceType(ServiceType.FARMER).stream()
                .map(userMapper::toFarmerResponseDto)
                .toList();
    }

    public boolean userExists(UUID userId) {
        return userRepository.existsByUserId(userId);
    }
}
