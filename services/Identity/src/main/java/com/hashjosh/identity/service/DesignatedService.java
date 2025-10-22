package com.hashjosh.identity.service;

import com.hashjosh.identity.dto.DesignatedRequestDTO;
import com.hashjosh.identity.dto.DesignatedResponseDTO;
import com.hashjosh.identity.entity.Designated;
import com.hashjosh.identity.exception.ApiException;
import com.hashjosh.identity.mapper.DesignatedMapper;
import com.hashjosh.identity.repository.DesignatedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignatedService {

    private final DesignatedRepository designatedRepository;
    private final DesignatedMapper designatedMapper;

    public List<DesignatedResponseDTO> getAllDesignated() {
        return designatedRepository.findAll().stream()
                .map(designatedMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public DesignatedResponseDTO getDesignatedById(UUID id) {
        return designatedRepository.findById(id)
                .map(designatedMapper::toResponseDto)
                .orElseThrow(() -> ApiException.notFound("Designated not found with id: " + id));
    }

    @Transactional
    public DesignatedResponseDTO createDesignated(DesignatedRequestDTO requestDTO) {
        validateDesignatedRequest(requestDTO);
        Designated designated = designatedMapper.toEntity(requestDTO);
        designated = designatedRepository.save(designated);
        return designatedMapper.toResponseDto(designated);
    }

    @Transactional
    public DesignatedResponseDTO updateDesignated(UUID id, DesignatedRequestDTO requestDTO) {
        validateDesignatedRequest(requestDTO);
        Designated existingDesignated = designatedRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Designated not found with id: " + id));

        designatedMapper.updateEntityFromDto(requestDTO, existingDesignated);
        existingDesignated = designatedRepository.save(existingDesignated);
        return designatedMapper.toResponseDto(existingDesignated);
    }

    @Transactional
    public void deleteDesignated(UUID id) {
        if (!designatedRepository.existsById(id)) {
            throw ApiException.notFound("Designated not found with id: " + id);
        }
        designatedRepository.deleteById(id);
    }

    private void validateDesignatedRequest(DesignatedRequestDTO requestDTO) {
        if (requestDTO.getTenantId() == null) {
            throw ApiException.badRequest("Tenant ID is required");
        }
        if (requestDTO.getUserId() == null) {
            throw ApiException.badRequest("User ID is required");
        }
        if (requestDTO.getType() == null) {
            throw ApiException.badRequest("Designation type is required");
        }
        if (requestDTO.getAssignedBy() == null) {
            throw ApiException.badRequest("Assigned By is required");
        }
    }
}