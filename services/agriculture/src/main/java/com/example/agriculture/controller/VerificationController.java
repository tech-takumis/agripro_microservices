package com.example.agriculture.controller;


import com.example.agriculture.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/verification")
public class VerificationController {

    private final VerificationService verificationService;
}
