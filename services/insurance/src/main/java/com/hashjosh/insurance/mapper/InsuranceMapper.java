package com.hashjosh.insurance.mapper;

import com.hashjosh.constant.pcic.enums.InsuranceStatus;
import com.hashjosh.insurance.dto.insurance.InsuranceRequestDTO;
import com.hashjosh.insurance.dto.insurance.InsuranceResponse;
import com.hashjosh.insurance.entity.Insurance;
import org.springframework.stereotype.Component;

@Component
public class InsuranceMapper {


    public Insurance toInsuranceEntity(InsuranceRequestDTO request){
        return Insurance.builder()
                .submissionId(request.getSubmissionId())
                .submittedBy(request.getSubmittedBy())
                .status(InsuranceStatus.valueOf(request.getStatus()))
                .build();
    }

    public InsuranceResponse toInsuranceResponse(Insurance insurance) {
        return InsuranceResponse.builder()
                .insuranceId(insurance.getId())
                .submissionId(insurance.getSubmissionId())
                .submittedBy(insurance.getSubmittedBy())
                .status(insurance.getStatus().name())
                .createdAt(insurance.getCreatedAt())
                .build();

    }
}
