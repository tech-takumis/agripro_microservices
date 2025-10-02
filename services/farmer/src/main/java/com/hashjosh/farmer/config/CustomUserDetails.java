package com.hashjosh.farmer.config;

import com.hashjosh.farmer.entity.Farmer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final Farmer farmer;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> rolesAndPermission = new ArrayList<>();

        farmer.getRoles().forEach(role -> rolesAndPermission.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        farmer.getRoles().forEach(
                role -> role.getPermissions()
                        .forEach(permission ->
                                rolesAndPermission.add(new SimpleGrantedAuthority(permission.getName()))
                        ));

        return rolesAndPermission;
    }

    @Override
    public String getPassword() {
        return farmer.getPassword();
    }

    @Override
    public String getUsername() {
        return farmer.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
