package com.example.agriculture.service;


import com.example.agriculture.mapper.VerificationRecordMapper;
import com.example.agriculture.repository.VerificationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class VerificationService {

    private final VerificationRecordRepository verificationRecordRepository;
    private final VerificationRecordMapper verificationRecordMapper;
}
