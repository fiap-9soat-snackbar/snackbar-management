package com.snackbar.iam.infrastructure.security;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamUserDetailsServiceTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private IamUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should load user details when user exists")
    void shouldLoadUserDetailsWhenUserExists() {
        // Given
        String cpf = "52998224725"; // Valid CPF without formatting
        User user = new User(
                "1",
                "Test User",
                "test@example.com",
                cpf,
                IamRole.CONSUMER,
                "encoded_password"
        );
        
        when(userGateway.findByCpf(cpf)).thenReturn(Optional.of(user));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(cpf);
        
        // Then
        assertNotNull(userDetails);
        assertEquals(cpf, userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        assertTrue(userDetails instanceof UserDetailsAdapter);
        
        UserDetailsAdapter adapter = (UserDetailsAdapter) userDetails;
        assertEquals(user, adapter.getUser());
        
        verify(userGateway).findByCpf(cpf);
    }
    
    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        // Given
        String cpf = "52998224725"; // Valid CPF without formatting
        when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
        
        // When/Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(cpf)
        );
        
        assertEquals("User not found with CPF: " + cpf, exception.getMessage());
        verify(userGateway).findByCpf(cpf);
    }
}
