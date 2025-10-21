
package com.hashjosh.transaction.mapper;

import com.hashjosh.transaction.dto.BeneficiaryDto;
import com.hashjosh.transaction.entity.Beneficiary;
import com.hashjosh.transaction.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeneficiaryMapper {

    private final ProgramRepository programRepository;

    public BeneficiaryDto toDto(Beneficiary beneficiary) {
        if (beneficiary == null) return null;

        return BeneficiaryDto.builder()
                .id(beneficiary.getId())
                .userId(beneficiary.getUserId())
                .type(beneficiary.getType())
                .programId(beneficiary.getProgram() != null ? beneficiary.getProgram().getId() : null)
                .build();
    }

    public Beneficiary toEntity(BeneficiaryDto dto) {
        if (dto == null) return null;

        return Beneficiary.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .type(dto.getType())
                .program(dto.getProgramId() != null ?
                        programRepository.findById(dto.getProgramId())
                                .orElse(null) : null)
                .build();
    }
}
