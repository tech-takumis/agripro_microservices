package com.hashjosh.pcic.dto;

import com.hashjosh.constant.pcic.enums.InspectionStatus;
import lombok.Data;

@Data
public class InspectionRequestDto {
    private InspectionStatus status;
    private String comments;
}
