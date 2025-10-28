package com.hashjosh.farmer.service;

import com.hashjosh.constant.farmer.FarmerReponse;
import com.hashjosh.farmer.exception.ApiException;
import com.hashjosh.farmer.mapper.FarmerMapper;
import com.hashjosh.farmer.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final FarmerMapper farmerMapper;
    public FarmerReponse findById(UUID farmerId) {
        return farmerMapper.toFarmerResponse(
                farmerRepository.findById(farmerId)
                        .orElseThrow(() ->
                                ApiException.notFound("Farmer with id %s not found".formatted(farmerId)))
        );
    }

    public List<FarmerReponse> findAll() {
        return farmerRepository.findAll()
                .stream()
                .map(farmerMapper::toFarmerResponse)
                .toList();
    }
}
