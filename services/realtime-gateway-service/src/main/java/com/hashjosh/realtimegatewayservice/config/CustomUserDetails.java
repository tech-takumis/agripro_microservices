package com.hashjosh.realtimegatewayservice.config;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

@Data
public class CustomUserDetails implements UserDetails, Principal {

    private final String token;
    private final String userId;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String phone;
    private Set<SimpleGrantedAuthority> authorities;
    public CustomUserDetails(String token, String userId, String username,
                             String firstname, String lastname,
                             String email, String phone, // Fixed parameter order to match field order
                             Set<SimpleGrantedAuthority> authorities) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.authorities = authorities;
    }

    @Override
    public String getName() {
        return userId; // Ensure we use userId for WebSocket user destination resolution
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
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
}
