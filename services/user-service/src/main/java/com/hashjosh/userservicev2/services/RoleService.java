package com.hashjosh.userservicev2.services;


import com.hashjosh.userservicev2.dto.RoleRequestDto;
import com.hashjosh.userservicev2.dto.RoleResponseDto;
import com.hashjosh.userservicev2.exceptions.RoleNotFoundException;
import com.hashjosh.userservicev2.mapper.RoleMapper;
import com.hashjosh.userservicev2.models.Authority;
import com.hashjosh.userservicev2.models.Role;
import com.hashjosh.userservicev2.models.User;
import com.hashjosh.userservicev2.repository.AuthorityRepository;
import com.hashjosh.userservicev2.repository.RoleRepository;
import com.hashjosh.userservicev2.repository.UserRepository;
import com.hashjosh.userservicev2.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private  final RoleMapper mapper;


    public RoleResponseDto createRole(RoleRequestDto roleRequestDto) {

        // Get or create the authorities if does not exist in the database
        // to avoid redundant value of authority in the database
        List<Authority> authority = authorityRepository.findAllById(roleRequestDto.permissionIds());

        // Same as the role we check first the database if the role already exist to avoid redundant
        // insertion of role in the database
        Role role = roleRepository.findByName(roleRequestDto.name())
                        .orElseGet(() -> roleRepository.save(
                                Role.builder()
                                        .name(roleRequestDto.name())
                                        .authorities(authority)
                                        .build()
                        ));

        role.setAuthorities(authority);
        role = roleRepository.save(role);

        return mapper.toRoleCreationResponse(role);
    }


    public List<RoleResponseDto> findAll() {
        return roleRepository.findAll().stream()
                    .map(mapper::toRoleResponse)
                    .collect(Collectors.toList());
    }

    public RoleResponseDto update(Long id, RoleRequestDto dto){

        roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(
                        "Role not found",
                        String.format("Role id %d not found.", id),
                        HttpStatus.NOT_FOUND.value()
                        ));


        Role role = mapper.updateRole(id, dto);
        return mapper.toRoleResponse(role);
    }

    public void delete(Long id){

        List<User> users = userRepository.findAll();
        userRepository.saveAll(users);
        roleRepository.deleteById(id);
    }



    public RoleResponseDto findRoleById(Long id) {

        Role role = roleRepository.findById(id).orElseThrow(
                () -> new RoleNotFoundException(
                        "Role not found",
                        String.format("Role id %d not found.", id),
                        HttpStatus.NOT_FOUND.value()
                        )
        );

        return mapper.toRoleResponse(role);
    }

    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll()
                .stream().map(mapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    public List<RoleResponseDto> saveAll(List<RoleRequestDto> roleRequestDtos) {

        List<Role> roles = mapper.toRoleList(roleRequestDtos);
        List<Role> savedRoles = roleRepository.saveAll(roles);
        return savedRoles.stream().map(mapper::toRoleResponse).collect(Collectors.toList());
    }
}
