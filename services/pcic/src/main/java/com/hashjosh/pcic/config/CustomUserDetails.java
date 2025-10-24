package com.hashjosh.pcic.config;

import com.hashjosh.pcic.entity.Pcic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private UUID userId;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String phoneNumber;
    private final Pcic pcic;
    private final String serviceId;
    private Set<SimpleGrantedAuthority> authorities;

    // Constructor for database-based authentication
    public CustomUserDetails(Pcic pcic, Set<SimpleGrantedAuthority> authorities) {
        this.pcic = pcic;
        this.authorities = authorities;
        this.userId = pcic.getId();
        this.username = pcic.getUsername();
        this.firstname = pcic.getFirstName();
        this.lastname = pcic.getLastName();
        this.email = pcic.getEmail();
        this.phoneNumber = pcic.getPhoneNumber();
        this.serviceId = null;
    }

    // Constructor for JWT claims-based authentication
    public CustomUserDetails(Map<String, Object> claims, Set<SimpleGrantedAuthority> authorities) {
        this.username = claims.get("sub").toString();
        this.authorities = authorities;
        this.firstname = (String) claims.get("firstname");
        this.lastname = (String) claims.get("lastname");
        this.email = (String) claims.get("email");
        this.phoneNumber = (String) claims.get("phoneNumber");
        this.userId = UUID.fromString((String) claims.get("userId"));
        this.pcic = null;
        this.serviceId = null;
    }

    // Constructor for internal service
    public CustomUserDetails(String serviceId,String userId, Set<SimpleGrantedAuthority> authorities) {
        this.username = serviceId;
        this.authorities = authorities;
        this.firstname = null;
        this.lastname = null;
        this.email = null;
        this.phoneNumber = null;
        this.userId = userId != null ? UUID.fromString(userId) : null;
        this.pcic = null;
        this.serviceId = serviceId;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return pcic != null ? pcic.getPassword() : null;
    }

    @Override
    public String getUsername() {
        if (pcic != null) {
            return pcic.getUsername();
        }
        if (serviceId != null) {
            return "internal-service-" + serviceId; // Unique username for internal services
        }
        throw new IllegalStateException("Neither pcic nor serviceId is set");
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
