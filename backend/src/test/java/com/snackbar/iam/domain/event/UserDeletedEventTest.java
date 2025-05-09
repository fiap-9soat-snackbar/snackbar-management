package com.snackbar.iam.domain.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDeletedEventTest {

    @Test
    void shouldCreateEventWithUserIdAndCpf() {
        // Given
        String userId = "user123";
        String userCpf = "123.456.789-00";
        
        // When
        UserDeletedEvent event = new UserDeletedEvent(userId, userCpf);
        
        // Then
        assertEquals(userId, event.getUserId());
        assertEquals("USER_DELETED", event.getEventType());
        assertEquals(userCpf, event.getUserCpf());
    }

    @Test
    void shouldInheritFromUserDomainEvent() {
        // Given
        String userId = "user123";
        String userCpf = "123.456.789-00";
        
        // When
        UserDeletedEvent event = new UserDeletedEvent(userId, userCpf);
        
        // Then
        assertTrue(event instanceof UserDomainEvent);
    }
}
