package com.snackbar.iam.infrastructure.security;

import com.snackbar.iam.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Adapter that bridges between our domain User entity and Spring Security's UserDetails.
 * This is part of the infrastructure layer in the clean architecture.
 * 
 * This adapter follows the adapter pattern to allow our domain User entity to be used
 * with Spring Security without polluting the domain model with framework-specific concerns.
 */
public class UserDetailsAdapter implements UserDetails {

    private final User user;
    private final List<GrantedAuthority> authorities;

    public UserDetailsAdapter(User user) {
        this.user = user;
        this.authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Using CPF as the username for Spring Security
        return user.getCpf();
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

    /**
     * Gets the underlying domain User entity.
     *
     * @return The domain User entity
     */
    public User getUser() {
        return user;
    }
}
