package com.hashjosh.insurance.dto.inspection;

import com.hashjosh.constant.pcic.enums.InspectionStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionResponse {
    private UUID id;
    private UUID insuranceId;
    private InspectionStatus status;
    private String comments;
}
