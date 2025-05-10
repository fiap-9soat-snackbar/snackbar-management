package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserDeletedEvent;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private IamDomainEventPublisher eventPublisher;

    private DeleteUserUseCase deleteUserUseCase;

    @BeforeEach
    void setUp() {
        deleteUserUseCase = new DeleteUserUseCase(userGateway, eventPublisher);
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        // Given: A user exists with the specified ID
        String userId = "1";
        String cpf = "52998224725";
        User user = new User(userId, "John Doe", "john@example.com", cpf, IamRole.CONSUMER, "password123");
        
        when(userGateway.findById(userId)).thenReturn(Optional.of(user));
        
        // When: Deleting the user
        deleteUserUseCase.deleteUser(userId);
        
        // Then: The user should be deleted and an event should be published
        verify(userGateway, times(1)).findById(userId);
        verify(userGateway, times(1)).deleteById(userId);
        
        ArgumentCaptor<UserDeletedEvent> eventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture());
        
        UserDeletedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(userId, capturedEvent.getUserId());
        assertEquals(cpf, capturedEvent.getUserCpf());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // Given: No user exists with the specified ID
        String userId = "nonexistent";
        
        when(userGateway.findById(userId)).thenReturn(Optional.empty());
        
        // When/Then: Deleting a non-existent user should throw an exception
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> deleteUserUseCase.deleteUser(userId)
        );
        
        // Then: The exception message should contain the user ID
        assertTrue(exception.getMessage().contains(userId));
        verify(userGateway, times(1)).findById(userId);
        verify(userGateway, never()).deleteById(anyString());
        verify(eventPublisher, never()).publish(any());
    }
}
