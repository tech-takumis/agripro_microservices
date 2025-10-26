package com.hashjosh.pcic.config;

import com.hashjosh.pcic.entity.Pcic;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails {
    private final UUID userId;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String phoneNumber;
    private final String serviceId;
    private final Set<SimpleGrantedAuthority> authorities;
    private final String password;
    private final Pcic pcic;

    // Database constructor
    public CustomUserDetails(Pcic pcic, Set<SimpleGrantedAuthority> authorities) {
        this.userId = pcic.getId();
        this.username = pcic.getUsername();
        this.firstname = pcic.getFirstName();
        this.lastname = pcic.getLastName();
        this.email = pcic.getEmail();
        this.phoneNumber = pcic.getPhoneNumber();
        this.password = pcic.getPassword();
        this.serviceId = null;
        this.authorities = authorities;
        this.pcic = pcic;
    }

    // JWT claims-based authentication constructor
    public CustomUserDetails(Map<String, Object> claims, Set<SimpleGrantedAuthority> authorities) {
        this.userId = claims.get("userId") != null ? UUID.fromString((String) claims.get("userId")) : null;
        this.username = claims.get("sub") != null ? claims.get("sub").toString() : null;
        this.firstname = (String) claims.get("firstname");
        this.lastname = (String) claims.get("lastname");
        this.email = (String) claims.get("email");
        this.phoneNumber = (String) claims.get("phoneNumber");
        this.password = null;
        this.serviceId = null;
        this.authorities = authorities;
        this.pcic = null;
    }

    // Internal service authentication constructor
    public CustomUserDetails(String serviceId, UUID userId, Set<SimpleGrantedAuthority> authorities) {
        this.userId = userId;
        this.username = serviceId;
        this.firstname = null;
        this.lastname = null;
        this.email = null;
        this.phoneNumber = null;
        this.password = null;
        this.serviceId = serviceId;
        this.authorities = authorities;
        this.pcic = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username != null ? username : serviceId;
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
