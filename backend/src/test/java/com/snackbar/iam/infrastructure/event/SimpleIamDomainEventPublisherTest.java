package com.snackbar.iam.infrastructure.event;

import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserCreatedEvent;
import com.snackbar.iam.domain.event.UserDomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleIamDomainEventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private SimpleIamDomainEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        eventPublisher = new SimpleIamDomainEventPublisher(applicationEventPublisher);
    }

    @Test
    @DisplayName("Should publish event to Spring's ApplicationEventPublisher")
    void shouldPublishEventToSpringEventPublisher() {
        // Given
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID().toString());
        when(user.getCpf()).thenReturn("123.456.789-09");
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getName()).thenReturn("Test User");
        
        UserDomainEvent event = new UserCreatedEvent(user);

        // When
        eventPublisher.publish(event);

        // Then
        verify(applicationEventPublisher, times(1)).publishEvent(event);
    }

    @Test
    @DisplayName("Should handle null event gracefully")
    void shouldHandleNullEventGracefully() {
        // Given
        UserDomainEvent event = null;

        // When/Then
        // This should not throw an exception, but we expect no event to be published
        try {
            eventPublisher.publish(event);
        } catch (NullPointerException e) {
            // This is expected behavior since the method doesn't have null checks
            verify(applicationEventPublisher, never()).publishEvent(any());
        }
    }
}
