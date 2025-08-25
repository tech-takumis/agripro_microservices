package com.hashjosh.userservicev2.controller;


import com.hashjosh.userservicev2.dto.RoleRequestDto;
import com.hashjosh.userservicev2.dto.RoleResponseDto;
import com.hashjosh.userservicev2.exceptions.RoleNotFoundException;
import com.hashjosh.userservicev2.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping("/role")
    public ResponseEntity<RoleResponseDto> create(
            @RequestBody RoleRequestDto roleRequestDto
    ){
        return new ResponseEntity<>(service.createRole(roleRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/roles")
    public ResponseEntity<List<RoleResponseDto>> createRoles(
            @RequestBody List<RoleRequestDto> roleRequestDtos
    ){
        return new ResponseEntity<>(service.saveAll(roleRequestDtos),HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponseDto>> getRoles(
    ){
        return new ResponseEntity<>(service.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<RoleResponseDto> findRole(@PathVariable Long id){
        return new ResponseEntity<>(service.findRoleById(id),HttpStatus.OK);
    }

    @PutMapping("/role/{id}")
    public ResponseEntity<RoleResponseDto> update(
            @PathVariable Long id, @RequestBody RoleRequestDto dto
    ) throws RoleNotFoundException {
        return new ResponseEntity<>(service.update(id, dto),HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/role/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws RoleNotFoundException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
