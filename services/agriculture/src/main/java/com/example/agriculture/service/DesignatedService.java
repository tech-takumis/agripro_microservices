package com.example.agriculture.service;

import com.example.agriculture.config.CustomUserDetails;
import com.example.agriculture.dto.DesignatedRequest;
import com.example.agriculture.dto.DesignatedResponse;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.Designated;
import com.example.agriculture.exception.ApiException;
import com.example.agriculture.repository.AgricultureRepository;
import com.example.agriculture.repository.DesignatedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignatedService {

    private final DesignatedRepository designatedRepository;
    private final AgricultureRepository agricultureRepository;
    public DesignatedResponse assignDesignated(DesignatedRequest request) {

        Agriculture userId = agricultureRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> ApiException.notFound("User not found: " + request.getUsername()));

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Designated designated = Designated.builder()
                .userId(userId)
                .designatedBy(userDetails.getAgriculture())
                .type(request.getType())
                .assignAt(LocalDateTime.now())
                .build();

        Designated saved = designatedRepository.save(designated);

        return DesignatedResponse.builder()
                .userId(saved.getUserId().getId())
                .serviceType(saved.getType().name())
                .build();
    }

    public DesignatedResponse getDesignatedByUsername(String username) {
        Designated designated = designatedRepository.findByUserIdUsernameContainingIgnoreCase(username)
                .orElseThrow(() -> ApiException.notFound("Designated user not found: " + username));

        return DesignatedResponse.builder()
                .userId(designated.getUserId().getId())
                .serviceType(designated.getType().name())
                .build();
    }

    public List<DesignatedResponse> findAll() {
        List<Designated> designatedList = designatedRepository.findAll();
        return designatedList.stream().map(designated -> DesignatedResponse.builder()
                .userId(designated.getUserId().getId())
                .serviceType(designated.getType().name())
                .build()).toList();
    }

    public DesignatedResponse getDesignatedByServiceType(String serviceType) {
        return findAll().stream()
                .filter(designatedResponse -> designatedResponse.getServiceType().equalsIgnoreCase(serviceType))
                .findFirst()
                .orElseThrow(() -> ApiException.notFound("Designated with service type not found: " + serviceType));
    }
}
