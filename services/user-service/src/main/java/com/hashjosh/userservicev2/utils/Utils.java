package com.hashjosh.userservicev2.utils;


import com.hashjosh.userservicev2.models.Authority;
import com.hashjosh.userservicev2.repository.AuthorityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class Utils {

    private final AuthorityRepository authorityRepository;

    public Utils(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    // private function
    public List<Authority> getAuthorities(Set<Authority> authorities) {
        return authorities.stream().map(
                this::getOrCreateAuthority
        ).collect(Collectors.toList());
    }
    public  Authority getOrCreateAuthority(Authority authority) {
        return authorityRepository.findByName(authority.getName()).orElseGet(
                () -> authorityRepository.save(
                        Authority.builder()
                                .name(authority.getName())
                                .build()
                ));
    }

}
