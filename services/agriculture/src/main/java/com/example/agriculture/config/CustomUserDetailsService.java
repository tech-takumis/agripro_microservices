package com.example.agriculture.config;

import com.example.agriculture.entity.Agriculture;
import com.example.agriculture.repository.AgricultureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AgricultureRepository agricultureRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Agriculture agriculture = agricultureRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Add role-based authorities
        agriculture.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getSlug().toUpperCase()));
            // Add permission-based authorities
            role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getSlug().toUpperCase()))
            );
        });

        // Create and return CustomUserDetails with the agriculture entity and authorities
        return new CustomUserDetails(agriculture, authorities);
    }
}
