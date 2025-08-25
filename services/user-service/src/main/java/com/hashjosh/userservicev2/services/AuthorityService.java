package com.hashjosh.userservicev2.services;


import com.hashjosh.userservicev2.dto.AuthorityDto;
import com.hashjosh.userservicev2.dto.AuthorityResponseDto;
import com.hashjosh.userservicev2.mapper.Authoritymapper;
import com.hashjosh.userservicev2.models.Authority;
import com.hashjosh.userservicev2.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final Authoritymapper authoritymapper;


    public List<AuthorityResponseDto> saveAuthorities(List<AuthorityDto> authorities) {

        List<Authority> authorityList = authoritymapper.toAuthorityList(authorities);
        List<Authority> savedAuthorities =  authorityRepository.saveAll(authorityList);

        return savedAuthorities.stream().map(
                authoritymapper::toAuthorityResponseDto
        ).collect(Collectors.toList());
    }

    public List<AuthorityResponseDto> findAll() {
        return authorityRepository.findAll().stream().map(
                authoritymapper::toAuthorityResponseDto
        ).collect(Collectors.toList());
    }


    public void delete(Long id) {
        authorityRepository.deleteById(id);
    }

}
