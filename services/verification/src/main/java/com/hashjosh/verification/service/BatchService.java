package com.hashjosh.verification.service;

import com.hashjosh.verification.dto.BatchRequestDTO;
import com.hashjosh.verification.dto.BatchResponseDTO;
import com.hashjosh.verification.entity.Batch;
import com.hashjosh.verification.mapper.BatchMapper;
import com.hashjosh.verification.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;

    public BatchResponseDTO createBatch(
            BatchRequestDTO request
    ) {
        log.info("Creating batch in the service layer");
        Batch batch = batchMapper.toEntity(request);

        return batchMapper.toBatchResponseDTO(batchRepository.save(batch));
    }

    public List<BatchResponseDTO> getAllBatches() {
        log.info("Retrieving all batches in the service layer");
        List<Batch> batches = batchRepository.findAll();
        return batches.stream()
                .map(batchMapper::toBatchResponseDTO)
                .toList();
    }
}
