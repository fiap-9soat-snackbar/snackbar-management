package com.snackbar.iam.domain.event;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserDomainEventTest {

    private static final String USER_ID = "user123";
    private static final String EVENT_TYPE = "TEST_EVENT";

    // Create a concrete implementation of the abstract class for testing
    private static class TestUserDomainEvent extends UserDomainEvent {
        public TestUserDomainEvent(String userId, String eventType) {
            super(userId, eventType);
        }
    }

    @Test
    void shouldCreateEventWithCorrectUserIdAndType() {
        // When
        UserDomainEvent event = new TestUserDomainEvent(USER_ID, EVENT_TYPE);
        
        // Then
        assertEquals(USER_ID, event.getUserId());
        assertEquals(EVENT_TYPE, event.getEventType());
    }

    @Test
    void shouldGenerateUniqueId() {
        // When
        UserDomainEvent event1 = new TestUserDomainEvent(USER_ID, EVENT_TYPE);
        UserDomainEvent event2 = new TestUserDomainEvent(USER_ID, EVENT_TYPE);
        
        // Then
        assertNotNull(event1.getId());
        assertNotNull(event2.getId());
        assertNotEquals(event1.getId(), event2.getId());
    }

    @Test
    void shouldSetTimestampToCurrentTime() {
        // Given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        
        // When
        UserDomainEvent event = new TestUserDomainEvent(USER_ID, EVENT_TYPE);
        
        // Then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        LocalDateTime timestamp = event.getTimestamp();
        
        assertNotNull(timestamp);
        assertTrue(timestamp.isAfter(before) || timestamp.isEqual(before));
        assertTrue(timestamp.isBefore(after) || timestamp.isEqual(after));
    }
}
