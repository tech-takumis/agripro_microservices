package com.hashjosh.farmer.config;

import com.hashjosh.farmer.entity.Farmer;
import com.hashjosh.farmer.repository.FarmerRepository;
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

    private final FarmerRepository farmerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Farmer farmer = farmerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Add role-based authorities
        farmer.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));

            // Add permission-based authorities
            role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getSlug().toUpperCase()))
            );
        });

        // Create CustomUserDetails with pre-populated authorities
        CustomUserDetails userDetails = new CustomUserDetails(farmer);
        // Set the authorities directly using reflection to avoid stack overflow
        try {
            java.lang.reflect.Field field = CustomUserDetails.class.getDeclaredField("authorities");
            field.setAccessible(true);
            field.set(userDetails, authorities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set authorities", e);
        }

        return userDetails;
    }
}
