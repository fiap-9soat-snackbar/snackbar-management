package com.snackbar.iam.infrastructure.event;

import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserCreatedEvent;
import com.snackbar.iam.domain.event.UserDeletedEvent;
import com.snackbar.iam.domain.event.UserUpdatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IamEventListenerTest {

    @InjectMocks
    private IamEventListener eventListener;
    
    @Mock
    private User mockUser;
    
    @Mock
    private UserCreatedEvent mockCreatedEvent;
    
    @Mock
    private UserUpdatedEvent mockUpdatedEvent;
    
    @Mock
    private UserDeletedEvent mockDeletedEvent;

    @Test
    @DisplayName("Should handle UserCreatedEvent without throwing exceptions")
    void shouldHandleUserCreatedEvent() {
        // Given
        String userId = UUID.randomUUID().toString();
        String cpf = "123.456.789-09";
        String email = "test@example.com";
        
        when(mockCreatedEvent.getUserId()).thenReturn(userId);
        when(mockCreatedEvent.getUserCpf()).thenReturn(cpf);
        when(mockCreatedEvent.getUserEmail()).thenReturn(email);

        // When/Then
        assertDoesNotThrow(() -> eventListener.handleUserCreatedEvent(mockCreatedEvent));
    }

    @Test
    @DisplayName("Should handle UserUpdatedEvent without throwing exceptions")
    void shouldHandleUserUpdatedEvent() {
        // Given
        String userId = UUID.randomUUID().toString();
        String cpf = "123.456.789-09";
        String email = "test@example.com";
        
        when(mockUpdatedEvent.getUserId()).thenReturn(userId);
        when(mockUpdatedEvent.getUserCpf()).thenReturn(cpf);
        when(mockUpdatedEvent.getUserEmail()).thenReturn(email);

        // When/Then
        assertDoesNotThrow(() -> eventListener.handleUserUpdatedEvent(mockUpdatedEvent));
    }

    @Test
    @DisplayName("Should handle UserDeletedEvent without throwing exceptions")
    void shouldHandleUserDeletedEvent() {
        // Given
        String userId = UUID.randomUUID().toString();
        String cpf = "123.456.789-09";
        
        when(mockDeletedEvent.getUserId()).thenReturn(userId);
        when(mockDeletedEvent.getUserCpf()).thenReturn(cpf);

        // When/Then
        assertDoesNotThrow(() -> eventListener.handleUserDeletedEvent(mockDeletedEvent));
    }

    @Test
    @DisplayName("Should handle generic UserDomainEvent without throwing exceptions")
    void shouldHandleGenericUserDomainEvent() {
        // Given
        String userId = UUID.randomUUID().toString();
        String eventType = "TEST_EVENT";
        
        when(mockCreatedEvent.getUserId()).thenReturn(userId);
        when(mockCreatedEvent.getEventType()).thenReturn(eventType);

        // When/Then
        assertDoesNotThrow(() -> eventListener.handleAllDomainEvents(mockCreatedEvent));
    }
}
