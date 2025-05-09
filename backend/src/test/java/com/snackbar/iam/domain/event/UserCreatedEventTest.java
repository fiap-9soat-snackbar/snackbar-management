package com.snackbar.iam.domain.event;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserCreatedEventTest {

    @Test
    void shouldCreateEventWithUserData() {
        // Given
        String userId = "user123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String cpf = "123.456.789-00";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";
        
        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setCpf(cpf);
        user.setRole(role);
        user.setPassword(password);
        
        // When
        UserCreatedEvent event = new UserCreatedEvent(user);
        
        // Then
        assertEquals(userId, event.getUserId());
        assertEquals("USER_CREATED", event.getEventType());
        assertEquals(cpf, event.getUserCpf());
        assertEquals(email, event.getUserEmail());
        assertEquals(name, event.getUserName());
    }

    @Test
    void shouldInheritFromUserDomainEvent() {
        // Given
        User user = new User();
        user.setId("user123");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setCpf("123.456.789-00");
        user.setRole(IamRole.CONSUMER);
        user.setPassword("password123");
        
        // When
        UserCreatedEvent event = new UserCreatedEvent(user);
        
        // Then
        assertTrue(event instanceof UserDomainEvent);
    }
}
