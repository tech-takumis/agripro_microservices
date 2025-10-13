package com.example.agriculture.mapper;

import com.example.agriculture.dto.beneficiary.BeneficiaryRequest;
import com.example.agriculture.dto.beneficiary.BeneficiaryResponse;
import com.example.agriculture.entity.Beneficiary;
import org.springframework.stereotype.Component;

@Component
public class BeneficiaryMapper {

    public BeneficiaryResponse toBeneficiaryResponse(Beneficiary beneficiary) {
        return BeneficiaryResponse.builder()
                .beneficiaryId(beneficiary.getId())
                .userId(beneficiary.getUserId())
                .type(beneficiary.getType())
                .build();
    }

    public Beneficiary toBeneficiaryEntity(BeneficiaryRequest request) {
        return Beneficiary.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .build();
    }
}
