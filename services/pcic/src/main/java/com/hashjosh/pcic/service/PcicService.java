package com.hashjosh.pcic.service;

import com.hashjosh.pcic.dto.user.PcicResponse;
import com.hashjosh.pcic.exception.ApiException;
import com.hashjosh.pcic.mapper.PcicMapper;
import com.hashjosh.pcic.repository.PcicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
                        .orElseThrow(() -> ApiException.notFound("Pcic not found"))
        );
    }


}
