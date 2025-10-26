package com.hashjosh.application.service;

import com.hashjosh.application.dto.batch.BatchRequestDTO;
import com.hashjosh.application.dto.batch.BatchResponseDTO;
import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.application.mapper.BatchMapper;
import com.hashjosh.application.model.ApplicationType;
import com.hashjosh.application.model.Batch;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import com.hashjosh.application.repository.BatchRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final ApplicationTypeRepository applicationTypeRepository;

    public List<BatchResponseDTO> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(batchMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BatchResponseDTO getBatchByName(String batchName) {
        return batchRepository.findByName(batchName)
                .map(batchMapper::toResponseDTO)
                .orElseThrow(() -> ApiException.notFound("Batch not found!"));
    }

    public BatchResponseDTO getBatchById(UUID batchId) {
        return batchRepository.findById(batchId)
                .map(batchMapper::toResponseDTO)
                .orElseThrow(() -> ApiException.notFound("Batch not found!"));
    }

    public BatchResponseDTO createBatch(BatchRequestDTO batch) {
        ApplicationType type = applicationTypeRepository.findById(batch.getApplicationTypeId())
                .orElseThrow(() -> ApiException.notFound("Application Type not found!"));

        Batch batch1 = Batch.builder()
                .name(batch.getName())
                .description(batch.getDescription())
                .isAvailable(batch.isAvailable())
                .maxApplications(batch.getMaxApplications())
                .startDate(batch.getStartDate())
                .endDate(batch.getEndDate())
                .applicationType(type)
                .build();

        return batchMapper.toResponseDTO(batchRepository.save(batch1));
    }

    public BatchResponseDTO updateBatch(
            BatchRequestDTO requestDTO,
            UUID batchId
            ) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> ApiException.notFound("Batch not found!"));

        Batch updatedBatch = Batch.builder()
                .id(batch.getId())
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .isAvailable(requestDTO.isAvailable())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .applicationType(batch.getApplicationType())
                .build();
        batchRepository.save(updatedBatch);
        return batchMapper.toResponseDTO(updatedBatch);
    }

    public void deleteBatch(UUID batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> ApiException.notFound("Batch not found!"));
        batchRepository.delete(batch);
    }

    public List<BatchResponseDTO> getBatchesByProvider(String providerName) {
        return batchRepository.findAllByApplicationType_Provider_Name(providerName).stream()
                .map(batchMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
