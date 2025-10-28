package com.hashjosh.pcic.mapper;

import com.hashjosh.pcic.dto.user.PcicResponse;
import com.hashjosh.pcic.entity.Pcic;
import org.springframework.stereotype.Component;

@Component
public class PcicMapper {
    public PcicResponse toPcicResponse(Pcic pcic) {
        return PcicResponse.builder()
                .id(pcic.getId())
                .username(pcic.getUsername())
                .firstName(pcic.getFirstName())
                .lastName(pcic.getLastName())
                .email(pcic.getEmail())
                .phoneNumber(pcic.getPhoneNumber())
                .address(pcic.getAddress())
                .build();
    }
}
