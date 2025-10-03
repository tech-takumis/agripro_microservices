package com.example.agriculture.dto;

import com.example.agriculture.enums.BatchStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BatchRequest {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BatchStatus status;
}
