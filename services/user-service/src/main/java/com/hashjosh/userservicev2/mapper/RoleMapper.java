package com.hashjosh.userservicev2.mapper;


import com.hashjosh.userservicev2.dto.RoleRequestDto;
import com.hashjosh.userservicev2.dto.RoleResponseDto;
import com.hashjosh.userservicev2.exceptions.RoleNotFoundException;
import com.hashjosh.userservicev2.models.Authority;
import com.hashjosh.userservicev2.models.Role;
import com.hashjosh.userservicev2.repository.AuthorityRepository;
import com.hashjosh.userservicev2.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleMapper {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;

    public RoleMapper(AuthorityRepository authorityRepository, RoleRepository roleRepository) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
    }


    public RoleResponseDto toRoleResponse(Role role) {
            return new RoleResponseDto(
                    role.getId(),
                    role.getName(),
                    role.getAuthorities().stream().map(
                        Authority::getName
                     ).collect(Collectors.toSet()));
    }

    public Role updateRole(Long id, RoleRequestDto dto) {

        List<Authority> authorities = authorityRepository.findAllById(dto.permissionIds());

        return Role.builder()
                .id(id)
                .name(dto.name())
                .authorities(authorities)
                .build();
    }


    public RoleResponseDto toRoleCreationResponse(Role role) {
        return new RoleResponseDto(
                role.getId(),
                role.getName(), role.getAuthorities().stream().map(
                Authority::getName
        ).collect(Collectors.toSet()));
    }

    public List<Role> toRoleList(List<RoleRequestDto> roleRequestDtos) {
        List<Role> roles = new ArrayList<>();

        for (RoleRequestDto roleRequestDto : roleRequestDtos) {
            List<Authority> authorities = new ArrayList<>();
            for(Long id: roleRequestDto.permissionIds()){
                Optional<Authority> authority = authorityRepository.findById(id);
                if(authority.isEmpty()){
                    throw new RoleNotFoundException(
                            "Authority id "+ id + " not found.",
                            String.format("Authority id %d not found.", id),
                            HttpStatus.NOT_FOUND.value()
                            );
                }

                authorities.add(authority.get());
            }

            Optional<Role> saveRole = roleRepository.findByName(roleRequestDto.name());

            if(saveRole.isEmpty()){
                Role role = Role.builder()
                        .name(roleRequestDto.name())
                        .authorities(authorities)
                        .build();

                roles.add(role);
            }
            else{
                saveRole.get().setAuthorities(authorities);
                roles.add(saveRole.get());
            }

        }
       return roles;
    }
}
