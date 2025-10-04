package com.hashjosh.pcic.service;

import com.hashjosh.pcic.dto.PcicResponse;
import com.hashjosh.pcic.exception.PcicException;
import com.hashjosh.pcic.mapper.PcicMapper;
import com.hashjosh.pcic.repository.PcicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PcicService {
    private final PcicRepository pcicRepository;
    private final PcicMapper pcicMapper;

    public List<PcicResponse> findAll() {
        return pcicRepository.findAll().stream()
                .map(pcicMapper::toPcicResponse)
                .toList();
    }


    public PcicResponse findById(UUID pcicId) {
        return pcicMapper.toPcicResponse(
                pcicRepository.findById(pcicId)
                        .orElseThrow(() -> new PcicException("Pcic id not found", HttpStatus.NOT_FOUND.value()))
        );
    }
}
