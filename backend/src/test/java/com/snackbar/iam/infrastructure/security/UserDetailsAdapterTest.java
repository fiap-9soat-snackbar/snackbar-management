package com.snackbar.iam.infrastructure.security;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsAdapterTest {

    @Test
    @DisplayName("Should create UserDetailsAdapter from User")
    void shouldCreateUserDetailsAdapterFromUser() {
        // Given
        User user = new User(
                "1",
                "Test User",
                "test@example.com",
                "52998224725", // Valid CPF without formatting
                IamRole.CONSUMER,
                "encoded_password"
        );
        
        // When
        UserDetailsAdapter adapter = new UserDetailsAdapter(user);
        
        // Then
        assertEquals(user, adapter.getUser());
        assertEquals(user.getCpf(), adapter.getUsername());
        assertEquals(user.getPassword(), adapter.getPassword());
    }
    
    @Test
    @DisplayName("Should map user role to Spring Security authority")
    void shouldMapUserRoleToSpringSecurityAuthority() {
        // Given
        User user = new User(
                "1",
                "Test User",
                "test@example.com",
                "52998224725", // Valid CPF without formatting
                IamRole.ADMIN,
                "encoded_password"
        );
        
        // When
        UserDetailsAdapter adapter = new UserDetailsAdapter(user);
        Collection<? extends GrantedAuthority> authorities = adapter.getAuthorities();
        
        // Then
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
    
    @Test
    @DisplayName("Should return true for all account status methods")
    void shouldReturnTrueForAllAccountStatusMethods() {
        // Given
        User user = new User(
                "1",
                "Test User",
                "test@example.com",
                "52998224725", // Valid CPF without formatting
                IamRole.CONSUMER,
                "encoded_password"
        );
        
        // When
        UserDetailsAdapter adapter = new UserDetailsAdapter(user);
        
        // Then
        assertTrue(adapter.isAccountNonExpired());
        assertTrue(adapter.isAccountNonLocked());
        assertTrue(adapter.isCredentialsNonExpired());
        assertTrue(adapter.isEnabled());
    }
}
