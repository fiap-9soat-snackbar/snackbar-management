package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get User By CPF Use Case Tests")
class GetUserByCpfUseCaseTest {

    @Mock
    private UserGateway userGateway;

    private GetUserByCpfUseCase getUserByCpfUseCase;

    @BeforeEach
    void setUp() {
        getUserByCpfUseCase = new GetUserByCpfUseCase(userGateway);
    }

    @Nested
    @DisplayName("When user exists")
    class WhenUserExists {
        
        @Test
        @DisplayName("Should return user when found by CPF")
        void shouldReturnUserWhenUserExists() {
            // Given: A user exists with the specified CPF
            String cpf = "52998224725";
            User expectedUser = new User("1", "John Doe", "john@example.com", cpf, IamRole.CONSUMER, "password123");
            
            when(userGateway.findByCpf(cpf)).thenReturn(Optional.of(expectedUser));
            
            // When: Getting user by CPF
            User actualUser = getUserByCpfUseCase.getUserByCpf(cpf);
            
            // Then: The correct user should be returned
            assertEquals(expectedUser, actualUser);
            verify(userGateway, times(1)).findByCpf(cpf);
        }
        
        @Test
        @DisplayName("Should return user with correct properties")
        void shouldReturnUserWithCorrectProperties() {
            // Given: A user exists with specific properties
            String id = "user-123";
            String name = "Jane Smith";
            String email = "jane@example.com";
            String cpf = "98765432100";
            IamRole role = IamRole.ADMIN;
            
            User expectedUser = new User(id, name, email, cpf, role, "password");
            
            when(userGateway.findByCpf(cpf)).thenReturn(Optional.of(expectedUser));
            
            // When: Getting user by CPF
            User actualUser = getUserByCpfUseCase.getUserByCpf(cpf);
            
            // Then: The user should have the correct properties
            assertEquals(id, actualUser.getId());
            assertEquals(name, actualUser.getName());
            assertEquals(email, actualUser.getEmail());
            assertEquals(cpf, actualUser.getCpf());
            assertEquals(role, actualUser.getRole());
        }
    }

    @Nested
    @DisplayName("When user does not exist")
    class WhenUserDoesNotExist {
        
        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowExceptionWhenUserDoesNotExist() {
            // Given: No user exists with the specified CPF
            String cpf = "52998224725";
            
            when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
            
            // When/Then: Getting user by CPF should throw an exception
            UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> getUserByCpfUseCase.getUserByCpf(cpf)
            );
            
            // Then: The exception message should contain the CPF
            assertTrue(exception.getMessage().contains(cpf));
            verify(userGateway, times(1)).findByCpf(cpf);
        }
        
        @Test
        @DisplayName("Should throw exception with descriptive message")
        void shouldThrowExceptionWithDescriptiveMessage() {
            // Given: No user exists with the specified CPF
            String cpf = "11122233344";
            
            when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
            
            // When/Then: Getting user by CPF should throw an exception
            UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> getUserByCpfUseCase.getUserByCpf(cpf)
            );
            
            // Then: The exception message should be descriptive
            String expectedMessage = "User not found with CPF: " + cpf;
            assertEquals(expectedMessage, exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {
        
        @Test
        @DisplayName("Should handle null CPF gracefully")
        void shouldHandleNullCpfGracefully() {
            // Given: A null CPF is provided
            String cpf = null;
            
            // When/Then: Getting user by null CPF should throw an exception
            assertThrows(
                IllegalArgumentException.class,
                () -> getUserByCpfUseCase.getUserByCpf(cpf)
            );
            
            // Then: The gateway should not be called
            verify(userGateway, never()).findByCpf(any());
        }
    }
}
