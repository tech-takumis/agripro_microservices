package com.example.agriculture.mapper;

import com.example.agriculture.dto.BatchRequest;
import com.example.agriculture.dto.BatchResponse;
import com.example.agriculture.entity.Batch;
import org.springframework.stereotype.Component;

@Component
public class BatchMapper {
    public Batch toEntity(BatchRequest request) {
        return Batch.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .build();
    }

    public BatchResponse toBatchResponse(Batch save) {
        return new BatchResponse(
                save.getId(),
                save.getName(),
                save.getStartDate(),
                save.getEndDate(),
                save.getStatus()
        );
    }
}
