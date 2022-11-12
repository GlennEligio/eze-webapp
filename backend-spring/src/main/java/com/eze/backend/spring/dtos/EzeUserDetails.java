package com.eze.backend.spring.dtos;

import com.eze.backend.spring.model.Account;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class EzeUserDetails implements UserDetails {

    private final Account account;
    private String fullName;

    public EzeUserDetails(Account account) {
        this.account = account;
        this.fullName = account.getFullName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(account.getType().getName()));
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return account.getActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return account.getActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return account.getActive();
    }

    @Override
    public boolean isEnabled() {
        return account.getActive();
    }
}
