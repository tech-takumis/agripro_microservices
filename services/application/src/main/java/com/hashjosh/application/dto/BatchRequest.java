package com.hashjosh.application.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Data
@Builder
public class BatchRequest {

    private String name;
    private UUID createdBy;
    private List<UUID>applicationIds;
}
