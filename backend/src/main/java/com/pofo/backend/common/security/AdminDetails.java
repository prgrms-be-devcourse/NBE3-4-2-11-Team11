package com.pofo.backend.common.security;

import com.pofo.backend.domain.admin.login.entitiy.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class AdminDetails implements UserDetails {

    private final Admin admin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return admin.getStatus() == Admin.Status.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return admin.getFailureCount() < 5; // 실패 횟수 5 이상이면 잠금
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return admin.getStatus() == Admin.Status.ACTIVE;
    }

    public Admin getAdmin() {
        return this.admin;
    }
}
