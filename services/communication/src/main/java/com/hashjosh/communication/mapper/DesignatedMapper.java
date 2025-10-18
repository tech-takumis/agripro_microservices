package com.hashjosh.communication.mapper;


import com.hashjosh.communication.dto.DesignatedResponse;
import com.hashjosh.communication.entity.DesignatedStaff;
import org.springframework.stereotype.Component;

@Component
public class DesignatedMapper {
    public DesignatedResponse toDesignatedResponseDto(DesignatedStaff designatedStaff) {
        return DesignatedResponse.builder()
                .userId(designatedStaff.getUserId())
                .serviceType(designatedStaff.getServiceType().name())
                .build();
    }
}
