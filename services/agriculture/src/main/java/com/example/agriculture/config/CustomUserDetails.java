package com.example.agriculture.config;

import com.example.agriculture.entity.Agriculture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final String token;
    private final Agriculture agriculture;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String serviceId; // For internal services

    // Constructor for internal services
    public CustomUserDetails(String serviceId, Collection<? extends GrantedAuthority> authorities) {
        this.token = null;
        this.agriculture = null;
        this.authorities = authorities != null ? authorities : Collections.emptyList();
        this.serviceId = serviceId;
    }

    // Constructor for JWT-based authentication
    public CustomUserDetails(Agriculture agriculture) {
        this.token = null;
        this.agriculture = agriculture;
        this.serviceId = null;
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        if (agriculture != null && agriculture.getRoles() != null) {
            agriculture.getRoles().forEach(role -> {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role.getSlug().toUpperCase()));
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission -> {
                        roles.add(new SimpleGrantedAuthority(permission.getSlug().toUpperCase()));
                    });
                }
            });
        }
        this.authorities = roles.isEmpty() ? Collections.emptyList() : roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return agriculture != null ? agriculture.getPassword() : null;
    }

    @Override
    public String getUsername() {
        if (agriculture != null) {
            return agriculture.getUsername();
        }
        if (serviceId != null) {
            return "internal-service-" + serviceId; // Unique username for internal services
        }
        throw new IllegalStateException("Neither agriculture nor serviceId is set");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Agriculture getAgriculture() {
        return agriculture;
    }
}