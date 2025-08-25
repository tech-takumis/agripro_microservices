package com.hashjosh.userservicev2.services;

import com.hashjosh.userservicev2.dto.RsbsaRequestDto;
import com.hashjosh.userservicev2.dto.RsbsaResponseDto;
import com.hashjosh.userservicev2.mapper.RsbsaMapper;
import com.hashjosh.userservicev2.models.Rsbsa;
import com.hashjosh.userservicev2.repository.RsbsaRepository;
import com.hashjosh.userservicev2.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RsbsaService {

    private final RsbsaRepository repository;
    private final UserRepository userRepository;
    private final RsbsaMapper mapper;

    public RsbsaService(RsbsaRepository repository, UserRepository userRepository,
                        RsbsaMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;

    }


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
}
