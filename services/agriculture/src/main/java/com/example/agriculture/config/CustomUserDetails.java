package com.example.agriculture.config;

import com.example.agriculture.entity.Agriculture;
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
    private final Set<SimpleGrantedAuthority> authorities;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final UUID userId;
    private final Agriculture agriculture;
    private final String serviceId;

    // Constructor for database-based authentication
    public CustomUserDetails(Agriculture agriculture, Set<SimpleGrantedAuthority> authorities) {
        this.username = agriculture.getUsername();
        this.authorities = authorities;
        this.firstName = agriculture.getFirstName();
        this.lastName = agriculture.getLastName();
        this.email = agriculture.getEmail();
        this.phoneNumber = agriculture.getPhoneNumber();
        this.userId = agriculture.getId();
        this.agriculture = agriculture;
        this.serviceId = null;
    }

    // Constructor for JWT claims-based authentication
    public CustomUserDetails(Map<String, Object> claims, Set<SimpleGrantedAuthority> authorities) {
        this.username = claims.get("sub").toString();
        this.authorities = authorities;
        this.firstName = (String) claims.get("firstname");
        this.lastName = (String) claims.get("lastname");
        this.email = (String) claims.get("email");
        this.phoneNumber = (String) claims.get("phoneNumber");
        this.userId = UUID.fromString((String) claims.get("userId"));
        this.agriculture = null;
        this.serviceId = null;
    }

    // Constructor for internal service
    public CustomUserDetails(String serviceId,String userId, Set<SimpleGrantedAuthority> authorities) {
        this.username = serviceId;
        this.authorities = authorities;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phoneNumber = null;
        this.userId = userId != null ? UUID.fromString(userId) : null;
        this.agriculture = null;
        this.serviceId = serviceId;
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
        if (serviceId != null) {
            return "internal-service-" + serviceId;
        }
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