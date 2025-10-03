package com.example.agriculture.service;

import com.example.agriculture.dto.BatchRequest;
import com.example.agriculture.dto.BatchResponse;
import com.example.agriculture.entity.Batch;
import com.example.agriculture.enums.BatchStatus;
import com.example.agriculture.exception.BatchException;
import com.example.agriculture.kafka.AgricultureProducer;
import com.example.agriculture.mapper.BatchMapper;
import com.example.agriculture.repository.BatchRepository;
import com.hashjosh.kafkacommon.agriculture.BatchCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final AgricultureProducer agricultureProducer;

    public BatchResponse createBatch(BatchRequest request) {
        Batch batch = batchMapper.toEntity(request);

        BatchResponse response = batchMapper.toBatchResponse(batchRepository.save(batch));

        // Publish  an event to notify the farmer that a new batch has been created
        BatchCreatedEvent batchCreatedEvent = new BatchCreatedEvent(
                response.getId(),
                response.getName(),
                response.getStartDate(),
                response.getEndDate() != null ? response.getEndDate() : LocalDateTime.now().plusMonths(6),
                response.getStatus().name()
        );
        agricultureProducer.publishEvent("batch-created", batchCreatedEvent);

        log.info("Created new batch with id: {}", response.getId());
        return response;
    }


    public BatchResponse findOpenNonExpiredBatch() {
        LocalDateTime currentDate = LocalDateTime.now();
        Batch batch = batchRepository.findOpenBatch(currentDate)
                .orElseThrow(() -> new BatchException("No open and non-expired batch found"));
        return batchMapper.toBatchResponse(batch);
    }
}
