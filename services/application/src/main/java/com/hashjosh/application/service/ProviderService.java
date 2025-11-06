package com.hashjosh.application.service;

import com.hashjosh.application.dto.provider.ProviderRequest;
import com.hashjosh.application.dto.provider.ProviderResponseDTO;
import com.hashjosh.application.exceptions.ApiException;
import com.hashjosh.application.model.ApplicationProvider;
import com.hashjosh.application.repository.ApplicationProviderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ApplicationProviderRepository providerRepository;

    @Transactional
    public void createProvider(@Valid ProviderRequest request) {

        boolean exists = providerRepository.existsByName(request.getName());

        if(exists){
            throw ApiException.badRequest("Provider already exists");
        }

        ApplicationProvider provider = ApplicationProvider.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        providerRepository.save(provider);

    }

    @Transactional
    public List<ProviderResponseDTO> getAllProviders() {

        List<ApplicationProvider> providers = providerRepository.findAll();

        return  providers.stream().map(
                provider -> {
                    return getProviderByName(provider.getName());
                }
        ).toList();
    }

    @Transactional
    public ProviderResponseDTO getProviderByName(String providerName) {

        ApplicationProvider provider = providerRepository.findByName(providerName)
                .orElseThrow(() -> ApiException.notFound("Provider not found"));

        return ProviderResponseDTO.builder()
                .id(provider.getId())
                .name(provider.getName())
                .description(provider.getDescription())
                .createdAt(provider.getCreatedAt())
                .build();
    }

    @Transactional
    public void updateProvider(String providerName, @Valid ProviderRequest request) {
        ApplicationProvider provider = providerRepository.findByName(providerName)
                .orElseThrow(() -> ApiException.notFound("Provider not found"));

        provider.setName(request.getName());
        provider.setDescription(request.getDescription());
        providerRepository.save(provider);
    }

    @Transactional
    public void deleteProvider(String providerName) {
        ApplicationProvider provider = providerRepository.findByName(providerName)
                .orElseThrow(() -> ApiException.notFound("Provider not found"));

        providerRepository.delete(provider);
    }
}
