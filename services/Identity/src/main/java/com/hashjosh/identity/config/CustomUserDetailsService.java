package com.hashjosh.identity.config;

import com.hashjosh.identity.entity.User;
import com.hashjosh.identity.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User agriculture = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Add role-based authorities
        agriculture.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole().getName()));
            // Add permission-based authorities
            role.getRole().getPermissions().forEach(rolePermission ->
                    authorities.add(new SimpleGrantedAuthority(rolePermission.getPermission().getName()))
            );
        });

        // Create and return CustomUserDetails with the agriculture entity and authorities
        return new CustomUserDetails(agriculture, authorities);
    }
}
