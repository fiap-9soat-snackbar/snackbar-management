package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserUpdatedEvent;
import com.snackbar.iam.domain.exceptions.DuplicateUserException;
import com.snackbar.iam.domain.exceptions.InvalidUserDataException;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
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
class UpdateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IamDomainEventPublisher eventPublisher;

    private UpdateUserUseCase updateUserUseCase;

    @BeforeEach
    void setUp() {
        updateUserUseCase = new UpdateUserUseCase(userGateway, passwordEncoder, eventPublisher);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Given: A user exists and valid update data is provided
        String userId = "1";
        String cpf = "52998224725";
        String existingEmail = "john@example.com";
        String existingPassword = "encodedPassword123";
        String newName = "John Updated";
        String newEmail = "john.updated@example.com";
        
        User existingUser = mock(User.class);
        when(existingUser.getCpf()).thenReturn(cpf);
        when(existingUser.getEmail()).thenReturn(existingEmail);
        when(existingUser.getPassword()).thenReturn(existingPassword);
        
        User updatedUserData = mock(User.class);
        when(updatedUserData.getName()).thenReturn(newName);
        when(updatedUserData.getEmail()).thenReturn(newEmail);
        when(updatedUserData.getCpf()).thenReturn(cpf);
        when(updatedUserData.getRole()).thenReturn(IamRole.CONSUMER);
        when(updatedUserData.getPassword()).thenReturn("");
        
        User savedUser = mock(User.class);
        when(savedUser.getId()).thenReturn(userId);
        when(savedUser.getName()).thenReturn(newName);
        when(savedUser.getEmail()).thenReturn(newEmail);
        when(savedUser.getCpf()).thenReturn(cpf);
        
        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userGateway.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userGateway.updateUser(any(User.class))).thenReturn(savedUser);
        
        // When: Updating the user
        User result = updateUserUseCase.updateUser(userId, updatedUserData);
        
        // Then: The user should be updated and an event should be published
        assertEquals(savedUser, result);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userGateway).updateUser(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertEquals(userId, capturedUser.getId());
        assertEquals(newName, capturedUser.getName());
        assertEquals(newEmail, capturedUser.getEmail());
        assertEquals(cpf, capturedUser.getCpf());
        assertEquals(existingPassword, capturedUser.getPassword()); // Password should not change
        
        ArgumentCaptor<UserUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(UserUpdatedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        UserUpdatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(userId, capturedEvent.getUserId());
        assertEquals(cpf, capturedEvent.getUserCpf());
        assertEquals(newEmail, capturedEvent.getUserEmail());
        assertEquals(newName, capturedEvent.getUserName());
    }

    @Test
    void shouldUpdateUserWithNewPassword() {
        // Given: A user exists and update data includes a new password
        String userId = "1";
        String cpf = "52998224725";
        String existingEmail = "john@example.com";
        String existingPassword = "encodedPassword123";
        String newPassword = "newPassword123";
        String encodedNewPassword = "encodedNewPassword123";
        
        User existingUser = mock(User.class);
        when(existingUser.getCpf()).thenReturn(cpf);
        when(existingUser.getEmail()).thenReturn(existingEmail);
        when(existingUser.getPassword()).thenReturn(existingPassword);
        
        User updatedUserData = mock(User.class);
        when(updatedUserData.getName()).thenReturn("John Doe");
        when(updatedUserData.getEmail()).thenReturn(existingEmail);
        when(updatedUserData.getCpf()).thenReturn(cpf);
        when(updatedUserData.getRole()).thenReturn(IamRole.CONSUMER);
        when(updatedUserData.getPassword()).thenReturn(newPassword);
        
        User savedUser = mock(User.class);
        
        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userGateway.updateUser(any(User.class))).thenReturn(savedUser);
        
        // When: Updating the user with a new password
        updateUserUseCase.updateUser(userId, updatedUserData);
        
        // Then: The password should be encoded and updated
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userGateway).updateUser(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertEquals(encodedNewPassword, capturedUser.getPassword());
        verify(passwordEncoder).encode(newPassword);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // Given: No user exists with the specified ID
        String userId = "nonexistent";
        User updatedUserData = mock(User.class);
        
        when(userGateway.findById(userId)).thenReturn(Optional.empty());
        
        // When/Then: Updating a non-existent user should throw an exception
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> updateUserUseCase.updateUser(userId, updatedUserData)
        );
        
        // Then: The exception message should contain the user ID
        assertTrue(exception.getMessage().contains(userId));
        verify(userGateway, never()).updateUser(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsChanged() {
        // Given: A user exists but the update tries to change the CPF
        String userId = "1";
        String existingCpf = "52998224725";
        String newCpf = "85399232081";
        
        User existingUser = mock(User.class);
        when(existingUser.getCpf()).thenReturn(existingCpf);
        
        User updatedUserData = mock(User.class);
        when(updatedUserData.getCpf()).thenReturn(newCpf);
        
        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
        
        // When/Then: Updating a user with a different CPF should throw an exception
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> updateUserUseCase.updateUser(userId, updatedUserData)
        );
        
        // Then: The exception message should indicate that CPF cannot be changed
        assertTrue(exception.getMessage().contains("CPF cannot be changed"));
        verify(userGateway, never()).updateUser(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsAlreadyInUse() {
        // Given: A user exists but the new email is already in use by another user
        String userId = "1";
        String cpf = "52998224725";
        String existingEmail = "john@example.com";
        String newEmail = "jane@example.com";
        
        User existingUser = mock(User.class);
        when(existingUser.getCpf()).thenReturn(cpf);
        when(existingUser.getEmail()).thenReturn(existingEmail);
        
        User updatedUserData = mock(User.class);
        when(updatedUserData.getCpf()).thenReturn(cpf);
        when(updatedUserData.getEmail()).thenReturn(newEmail);
        
        User otherUser = mock(User.class);
        when(otherUser.getId()).thenReturn("2"); // Different ID
        
        when(userGateway.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userGateway.findByEmail(newEmail)).thenReturn(Optional.of(otherUser));
        
        // When/Then: Updating a user with an email already in use should throw an exception
        DuplicateUserException exception = assertThrows(
            DuplicateUserException.class,
            () -> updateUserUseCase.updateUser(userId, updatedUserData)
        );
        
        // Then: The exception message should contain the email
        assertTrue(exception.getMessage().contains(newEmail));
        verify(userGateway, never()).updateUser(any());
        verify(eventPublisher, never()).publish(any());
    }
}
