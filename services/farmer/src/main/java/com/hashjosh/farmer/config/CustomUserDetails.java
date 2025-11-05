package com.hashjosh.farmer.config;

import com.hashjosh.farmer.entity.Farmer;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Set<SimpleGrantedAuthority> authorities;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final UUID userId;
    private final Farmer farmer; // Keep for backward compatibility

    public CustomUserDetails(Farmer farmer) {
        this.username = farmer.getUsername();
        this.password = farmer.getPassword(); // Store the password
        this.authorities = Set.of();  // Will be set separately
        this.firstName = farmer.getFirstName();
        this.lastName = farmer.getLastName();
        this.email = farmer.getEmail();
        this.phoneNumber = farmer.getPhoneNumber();
        this.userId = farmer.getId();
        this.farmer = farmer;
    }

    // Constructor for JWT claims-based authentication
    public CustomUserDetails(Map<String, Object> claims, Set<SimpleGrantedAuthority> authorities) {
        this.username = claims.get("sub").toString();
        this.password = null; // No password needed for JWT-based auth
        this.authorities = authorities;
        this.firstName = (String) claims.get("firstname");
        this.lastName = (String) claims.get("lastname");
        this.email = (String) claims.get("email");
        this.phoneNumber = (String) claims.get("phoneNumber");
        this.userId = UUID.fromString((String) claims.get("userId"));
        this.farmer = null;
    }

    // Constructor for internal service
    public CustomUserDetails(String serviceId,UUID userId, Set<SimpleGrantedAuthority> authorities) {
        this.username = serviceId;
        this.password = null; // No password needed for internal service auth
        this.authorities = authorities;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phoneNumber = null;
        this.userId = userId;
        this.farmer = null;
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
