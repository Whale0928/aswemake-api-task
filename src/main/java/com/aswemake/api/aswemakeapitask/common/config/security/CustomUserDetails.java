package com.aswemake.api.aswemakeapitask.common.config.security;

import com.aswemake.api.aswemakeapitask.domain.user.Users;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
public class CustomUserDetails implements UserDetails {

    private Users users;

    public CustomUserDetails(Users users) {
        if (users == null) {
            throw new IllegalArgumentException("Users object cannot be null");
        }
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.users.getRole().getAuthority()));
        return authorities;
    }


    @Override
    public String getPassword() {
        if (users != null) {
            return users.getPassword();
        }
        throw new UsernameNotFoundException("No user found.");
    }

    @Override
    public String getUsername() {
        if (users != null) {
            return users.getName();
        }
        throw new UsernameNotFoundException("No user found.");
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

