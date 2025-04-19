package com.snackbar.product.domain.event;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventTest {

    // Concrete implementation of the abstract DomainEvent for testing
    private static class TestDomainEvent extends DomainEvent {
        public TestDomainEvent() {
            super();
        }
    }

    @Test
    void shouldGenerateEventIdAutomatically() {
        // Arrange & Act
        DomainEvent event = new TestDomainEvent();
        
        // Assert
        assertNotNull(event.getEventId());
        assertFalse(event.getEventId().isEmpty());
    }

    @Test
    void shouldSetOccurredOnToCurrentTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        
        // Act
        DomainEvent event = new TestDomainEvent();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        
        // Assert
        assertNotNull(event.getOccurredOn());
        assertTrue(event.getOccurredOn().isAfter(before) || event.getOccurredOn().isEqual(before));
        assertTrue(event.getOccurredOn().isBefore(after) || event.getOccurredOn().isEqual(after));
    }

    @Test
    void differentEventsShouldHaveDifferentIds() {
        // Arrange & Act
        DomainEvent event1 = new TestDomainEvent();
        DomainEvent event2 = new TestDomainEvent();
        
        // Assert
        assertNotEquals(event1.getEventId(), event2.getEventId());
    }
}
