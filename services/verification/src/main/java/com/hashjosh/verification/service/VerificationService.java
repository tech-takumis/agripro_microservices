package com.hashjosh.verification.service;

import com.hashjosh.verification.dto.VerificationRequestDTO;
import com.hashjosh.verification.dto.VerificationResponseDTO;
import com.hashjosh.verification.entity.VerificationRecord;
import com.hashjosh.verification.exception.ApiException;
import com.hashjosh.verification.mapper.VerificationMapper;
import com.hashjosh.verification.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final VerificationMapper verificationMapper;

    public List<VerificationResponseDTO> getAllVerifications() {
        return verificationRepository.findAll().stream()
                .map(verificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public VerificationResponseDTO getVerificationById(UUID id) {
        return verificationRepository.findById(id)
                .map(verificationMapper::toResponseDto)
                .orElseThrow(() -> ApiException.notFound("Verification not found with id: " + id));
    }

    public List<VerificationResponseDTO> getVerificationsBySubmissionId(UUID submissionId) {
        return verificationRepository.findBySubmissionId(submissionId).stream()
                .map(verificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VerificationResponseDTO createVerification(VerificationRequestDTO requestDTO) {
        validateVerificationRequest(requestDTO);
        VerificationRecord verificationRecord = verificationMapper.toEntity(requestDTO);
        verificationRecord = verificationRepository.save(verificationRecord);
        return verificationMapper.toResponseDto(verificationRecord);
    }

    @Transactional
    public VerificationResponseDTO updateVerification(UUID id, VerificationRequestDTO requestDTO) {
        validateVerificationRequest(requestDTO);
        VerificationRecord existingRecord = verificationRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Verification not found with id: " + id));
        
        verificationMapper.updateEntityFromDto(requestDTO, existingRecord);
        existingRecord = verificationRepository.save(existingRecord);
        return verificationMapper.toResponseDto(existingRecord);
    }

    @Transactional
    public void deleteVerification(UUID id) {
        if (!verificationRepository.existsById(id)) {
            throw ApiException.notFound("Verification not found with id: " + id);
        }
        verificationRepository.deleteById(id);
    }

    private void validateVerificationRequest(VerificationRequestDTO requestDTO) {
        if (requestDTO.getSubmissionId() == null) {
            throw ApiException.badRequest("Submission ID is required");
        }
        if (requestDTO.getUploadedBy() == null) {
            throw ApiException.badRequest("Uploaded By is required");
        }
        if (requestDTO.getVerificationType() == null || requestDTO.getVerificationType().trim().isEmpty()) {
            throw ApiException.badRequest("Verification type is required");
        }
    }
}