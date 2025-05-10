package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserCreatedEvent;
import com.snackbar.iam.domain.exceptions.DuplicateUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IamDomainEventPublisher eventPublisher;

    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        registerUserUseCase = new RegisterUserUseCase(userGateway, passwordEncoder, eventPublisher);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given: Valid user data and no existing users with the same CPF or email
        String id = "1";
        String name = "John Doe";
        String email = "john@example.com";
        String cpf = "52998224725";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        
        User inputUser = mock(User.class);
        when(inputUser.getCpf()).thenReturn(cpf);
        when(inputUser.getEmail()).thenReturn(email);
        when(inputUser.getPassword()).thenReturn(password);
        
        User createdUser = mock(User.class);
        when(createdUser.getId()).thenReturn(id);
        when(createdUser.getName()).thenReturn(name);
        when(createdUser.getEmail()).thenReturn(email);
        when(createdUser.getCpf()).thenReturn(cpf);
        
        when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
        when(userGateway.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userGateway.createUser(inputUser)).thenReturn(createdUser);
        
        // When: Registering a new user
        User result = registerUserUseCase.registerUser(inputUser);
        
        // Then: The user should be registered with an encoded password and an event should be published
        verify(inputUser).setPassword(encodedPassword);
        verify(userGateway).createUser(inputUser);
        assertEquals(createdUser, result);
        
        ArgumentCaptor<UserCreatedEvent> eventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        UserCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(id, capturedEvent.getUserId());
        assertEquals(cpf, capturedEvent.getUserCpf());
        assertEquals(email, capturedEvent.getUserEmail());
        assertEquals(name, capturedEvent.getUserName());
    }

    @Test
    void shouldThrowExceptionWhenUserWithSameCpfExists() {
        // Given: A user with the same CPF already exists
        String cpf = "52998224725";
        
        User inputUser = mock(User.class);
        when(inputUser.getCpf()).thenReturn(cpf);
        
        User existingUser = mock(User.class);
        when(userGateway.findByCpf(cpf)).thenReturn(Optional.of(existingUser));
        
        // When/Then: Registering a user with an existing CPF should throw an exception
        DuplicateUserException exception = assertThrows(
            DuplicateUserException.class,
            () -> registerUserUseCase.registerUser(inputUser)
        );
        
        // Then: The exception message should contain the CPF
        assertTrue(exception.getMessage().contains(cpf));
        verify(userGateway, never()).createUser(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenUserWithSameEmailExists() {
        // Given: A user with the same email already exists
        String cpf = "52998224725";
        String email = "john@example.com";
        
        User inputUser = mock(User.class);
        when(inputUser.getCpf()).thenReturn(cpf);
        when(inputUser.getEmail()).thenReturn(email);
        
        when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
        
        User existingUser = mock(User.class);
        when(userGateway.findByEmail(email)).thenReturn(Optional.of(existingUser));
        
        // When/Then: Registering a user with an existing email should throw an exception
        DuplicateUserException exception = assertThrows(
            DuplicateUserException.class,
            () -> registerUserUseCase.registerUser(inputUser)
        );
        
        // Then: The exception message should contain the email
        assertTrue(exception.getMessage().contains(email));
        verify(userGateway, never()).createUser(any());
        verify(eventPublisher, never()).publish(any());
    }
}
