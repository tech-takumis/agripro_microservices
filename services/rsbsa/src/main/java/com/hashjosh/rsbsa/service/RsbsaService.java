package com.hashjosh.rsbsa.service;

import com.hashjosh.rsbsa.dto.RsbsaRequestDto;
import com.hashjosh.rsbsa.dto.RsbsaResponseDto;
import com.hashjosh.rsbsa.entity.Rsbsa;
import com.hashjosh.rsbsa.exception.ApiException;
import com.hashjosh.rsbsa.mapper.RsbsaMapper;
import com.hashjosh.rsbsa.repository.RsbsaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RsbsaService {
    private final RsbsaRepository repository;
    private final RsbsaMapper mapper;


    public RsbsaResponseDto save(RsbsaRequestDto dto) {
        Rsbsa rsbsa = mapper.dtoToRsbsa(dto);
        return  mapper.rsbsaToReponseDto(repository.save(rsbsa));
    }

    public List<RsbsaResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::rsbsaToReponseDto)
                .collect(Collectors.toList());
    }

    public RsbsaResponseDto findByRsbsaId(
            String rsbaId,
            HttpServletRequest request
            ) {
        Optional<Rsbsa> rsbsaOptional = repository.findByRsbsaIdEqualsIgnoreCase(rsbaId);

        if(rsbsaOptional.isEmpty()){
            throw ApiException.notFound(
                    "RSBSA with ID " + rsbaId + " not found"
            );
        }
        return mapper.rsbsaToReponseDto(
            rsbsaOptional.get()
        );
    }
}
