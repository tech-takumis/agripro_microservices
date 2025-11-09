package com.hashjosh.insurance.mapper;

import com.hashjosh.insurance.dto.inspection.InspectionResponse;
import com.hashjosh.insurance.entity.InspectionRecord;
import org.springframework.stereotype.Component;

@Component
public class InspectionMapper {
    public InspectionResponse toInspectionResposeDTO(InspectionRecord inspectionRecord) {
        return InspectionResponse.builder()
                .id(inspectionRecord.getId())
                .insuranceId(inspectionRecord.getInsurance().getId())
                .status(inspectionRecord.getStatus())
                .comments(inspectionRecord.getComments())
                .build();
    }
}
