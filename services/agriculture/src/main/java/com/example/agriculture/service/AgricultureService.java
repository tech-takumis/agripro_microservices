package com.example.agriculture.service;

import com.example.agriculture.dto.auth.AgricultureResponseDto;
import com.example.agriculture.dto.rbac.PermissionResponse;
import com.example.agriculture.dto.rbac.RoleResponse;
import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.entity.Permission;
import com.example.agriculture.entity.Role;
import com.example.agriculture.mapper.UserMapper;
import com.example.agriculture.repository.AgricultureRepository;
import com.example.agriculture.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgricultureService {
    private final AgricultureRepository agricultureRepository;
    private final PermissionRepository permissionRepository;
    private final UserMapper userMapper;

    private Agriculture getUserById(UUID userId){
        return agricultureRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Agriculturist not found"));
    }

    private  Permission getPermissionById(UUID permissionId){
       return permissionRepository.findById(permissionId)
               .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
    }

    public Page<AgricultureResponseDto> getAll(String search, Pageable pageable) {
        Page<Agriculture> result;
        if(search != null && !search.isEmpty()){
            result = agricultureRepository.search(search,pageable);
        }else{
            result = agricultureRepository.findAll(pageable);
        }

        return result.map(userMapper::toAgricultureResponseDto);
    }

    public void assignDirectPermission(UUID userId,UUID permissionId){
       Agriculture agriculture = getUserById(userId);
        Permission permission = getPermissionById(permissionId);

        agriculture.assignDirectPermission(permission);
    }

    public Set<String> getEffectivePermissions(UUID userId){
        Agriculture agriculture = getUserById(userId);
        return agriculture.getEffectivePermissions();
    }
    public AgricultureResponseDto getById(UUID id) {
        Agriculture agri = getUserById(id);
        return userMapper.toAgricultureResponseDto(agri);
    }

    public void delete(UUID id) {
        if (!agricultureRepository.existsById(id)) {
            throw new EntityNotFoundException("Agriculture user not found");
        }
        agricultureRepository.deleteById(id);
    }

    public String getAgricultureName(UUID id) {
        Agriculture agri = getUserById(id);
        return agri.getFirstName() + " " + agri.getLastName();
    }
}
