package com.hashjosh.userservicev2.mapper;


import com.hashjosh.userservicev2.dto.AuthorityDto;
import com.hashjosh.userservicev2.dto.AuthorityResponseDto;
import com.hashjosh.userservicev2.models.Authority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Authoritymapper {
    public AuthorityResponseDto toAuthorityResponseDto(Authority authority) {
        return new AuthorityResponseDto(
                authority.getId(), authority.getName()
        );
    }

    public List<Authority> toAuthorityList(List<AuthorityDto> authorities) {
        return authorities.stream().map(
            authority -> Authority.builder()
                    .name(authority.name())
                    .build()
        ).collect(Collectors.toList());
    }
}
