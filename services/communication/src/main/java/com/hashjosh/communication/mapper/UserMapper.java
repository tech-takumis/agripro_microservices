package com.hashjosh.communication.mapper;

import com.hashjosh.communication.dto.FarmerResponseDto;
import com.hashjosh.communication.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public FarmerResponseDto toFarmerResponseDto(User users) {
        return FarmerResponseDto.builder()
                .id(users.getId())
                .userId(users.getUserId())
                .username(users.getUsername())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .middleName(users.getMiddleName())
                .phoneNumber(users.getPhoneNumber())
                .email(users.getEmail())
                .serviceType(users.getServiceType().name())
                .createdAt(users.getCreatedAt())
                .build();
    }
}
