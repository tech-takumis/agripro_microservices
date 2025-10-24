package com.hashjosh.farmer.mapper;

import com.hashjosh.constant.farmer.FarmerReponse;
import com.hashjosh.farmer.entity.Farmer;
import org.springframework.stereotype.Component;

@Component
public class FarmerMapper {
    public FarmerReponse toFarmerResponse(Farmer farmer) {
        return FarmerReponse.builder()
                .id(farmer.getId())
                .username(farmer.getUsername())
                .firstName(farmer.getFirstName())
                .lastName(farmer.getLastName())
                .email(farmer.getEmail())
                .phoneNumber(farmer.getPhoneNumber())
                .build();
    }
}
