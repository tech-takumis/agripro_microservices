package com.hashjosh.rsbsa;

import com.hashjosh.rsbsa.dto.RsbsaRequestDto;
import com.hashjosh.rsbsa.dto.RsbsaResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.bootstrap.HttpServer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
            throw new RsbsaNotFoundException(
                    "Rsbsa with id " + rsbaId + " not found",
                    HttpStatus.NOT_FOUND.value(),
                    request.getRequestURI()
            );
        }
        return mapper.rsbsaToReponseDto(
            rsbsaOptional.get()
        );
    }
}
