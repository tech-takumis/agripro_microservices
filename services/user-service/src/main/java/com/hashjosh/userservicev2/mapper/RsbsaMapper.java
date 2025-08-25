package com.hashjosh.userservicev2.mapper;


import com.hashjosh.userservicev2.dto.RsbsaRequestDto;
import com.hashjosh.userservicev2.dto.RsbsaResponseDto;
import com.hashjosh.userservicev2.models.Rsbsa;
import com.hashjosh.userservicev2.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RsbsaMapper {

    private final PasswordEncoder passwordEncoder;

    public RsbsaMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User rsbsaToUser(Rsbsa rsbsa) {
        return  User.builder()
                .username(rsbsa.getRsbsaId())
                .email(rsbsa.getEmail())
                .password(passwordEncoder.encode(rsbsa.getDateOfBirth().toString()))
                .firstName(rsbsa.getFirstName())
                .lastName(rsbsa.getLastName())
                .build();
    }

    public Rsbsa dtoToRsbsa(RsbsaRequestDto dto) {
        return Rsbsa.builder()
                .rsbsaId(dto.rsbsaId())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .middleName(dto.middleName())
                .gender(dto.gender())
                .civilStatus(dto.civilStatus())
                .address(dto.address())
                .barangay(dto.barangay())
                .municipality(dto.municipality())
                .province(dto.province())
                .contactNumber(dto.contactNumber())
                .email(dto.email())
                .dateOfBirth(LocalDate.parse(dto.dateOfBirth()))
                .farmingType(dto.farmingType())
                .primaryCrop(dto.primaryCrop())
                .secondaryCrop(dto.secondaryCrop())
                .farmArea(dto.farmArea())
                .farmLocation(String.valueOf(dto.farmLocation()))
                .tenureStatus(dto.tenureStatus())
                .sourceOfIncome(dto.sourceOfIncome())
                .estimatedIncome(dto.estimatedIncome())
                .householdSize(dto.householdSize())
                .educationLevel(String.valueOf(dto.educationLevel()))
                .withDisability(dto.withDisability())
                .build();
    }

    public RsbsaResponseDto rsbsaToReponseDto(Rsbsa save) {
        return  new RsbsaResponseDto(
                save.getRsbsaId(),save.getFirstName(), save.getLastName(), save.getMiddleName(),
                save.getGender(), save.getCivilStatus(),save.getAddress(), save.getBarangay(),
                save.getMunicipality(),save.getProvince(),save.getContactNumber(),save.getEmail(),
                save.getFarmingType(), save.getPrimaryCrop(), save.getSecondaryCrop(), save.getDateOfBirth().toString() , save.getFarmArea(),
                save.getFarmLocation(), save.getTenureStatus(), save.getSourceOfIncome(), save.getEstimatedIncome(),
                save.getHouseholdSize(), save.getEducationLevel(),save.isWithDisability()
        );
    }
}
