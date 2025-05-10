package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByCpfUseCaseTest {

    @Mock
    private UserGateway userGateway;

    private GetUserByCpfUseCase getUserByCpfUseCase;

    @BeforeEach
    void setUp() {
        getUserByCpfUseCase = new GetUserByCpfUseCase(userGateway);
    }

    @Test
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
}
