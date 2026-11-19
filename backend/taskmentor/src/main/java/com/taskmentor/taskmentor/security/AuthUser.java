package com.taskmentor.taskmentor.security;

import com.taskmentor.taskmentor.enums.AccountType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * The authenticated principal. Controllers receive it with {@code @AuthenticationPrincipal},
 * so they never have to look the user up again by email.
 */
public record AuthUser(Long userId, String email, String passwordHash, AccountType role) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /** Excludes the password hash so the principal can never leak it into logs. */
    @Override
    public String toString() {
        return "AuthUser[userId=" + userId + ", role=" + role + "]";
    }
}
