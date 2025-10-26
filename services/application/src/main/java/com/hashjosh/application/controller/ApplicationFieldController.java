package com.hashjosh.application.controller;

import com.hashjosh.application.dto.fields.ApplicationFieldsRequestDto;
import com.hashjosh.application.service.ApplicationFieldsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/application/fields")
@RequiredArgsConstructor
public class ApplicationFieldController {

    private final ApplicationFieldsService applicationFieldService;

    @PostMapping("/{section-id}")
    public ResponseEntity<String> create(
            ApplicationFieldsRequestDto dto,
            @PathVariable("section-id") UUID sectionId
    ){
        applicationFieldService.create(dto,sectionId);
        return new ResponseEntity<>("Application field created successfully", HttpStatus.CREATED);
    }
}
