package com.example.agriculture.service;

import com.example.agriculture.dto.AgricultureResponseDto;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.Role;
import com.example.agriculture.repository.AgricultureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgricultureService {
    private final AgricultureRepository agricultureRepository;

    public Page<AgricultureResponseDto> getAll(String search, Pageable pageable) {
        Page<Agriculture> result;
        if(search != null && !search.isEmpty()){
            result = agricultureRepository.search(search,pageable);
        }else{
            result = agricultureRepository.findAll(pageable);
        }

        return result.map(this::toResponseDto);
    }

    public AgricultureResponseDto getById(UUID id) {
        Agriculture agri = agricultureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agriculture user not found"));
        return toResponseDto(agri);
    }
    public void delete(UUID id) {
        if (!agricultureRepository.existsById(id)) {
            throw new EntityNotFoundException("Agriculture user not found");
        }
        agricultureRepository.deleteById(id);
    }
    private AgricultureResponseDto toResponseDto(Agriculture entity) {
        return AgricultureResponseDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .roles(entity.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
