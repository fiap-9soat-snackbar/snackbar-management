package com.snackbar.infrastructure.messaging.sqs.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SQSMessageTest {

    @Test
    void defaultConstructor_shouldInitializeMessageIdAndTimestamp() {
        // Act
        TestSQSMessage message = new TestSQSMessage();
        
        // Assert
        assertNotNull(message.getMessageId());
        assertTrue(UUID.fromString(message.getMessageId()) != null); // Verify it's a valid UUID
        assertNotNull(message.getTimestamp());
        assertNull(message.getEventType());
    }
    
    @Test
    void constructorWithEventType_shouldInitializeAllFields() {
        // Arrange
        String eventType = "TEST_EVENT";
        
        // Act
        TestSQSMessage message = new TestSQSMessage(eventType);
        
        // Assert
        assertNotNull(message.getMessageId());
        assertEquals(eventType, message.getEventType());
        assertNotNull(message.getTimestamp());
    }
    
    @Test
    void constructorWithAllFields_shouldSetAllFields() {
        // Arrange
        String messageId = UUID.randomUUID().toString();
        String eventType = "TEST_EVENT";
        Instant timestamp = Instant.now();
        
        // Act
        TestSQSMessage message = new TestSQSMessage(messageId, eventType, timestamp);
        
        // Assert
        assertEquals(messageId, message.getMessageId());
        assertEquals(eventType, message.getEventType());
        assertEquals(timestamp, message.getTimestamp());
    }
    
    @Test
    void setters_shouldUpdateFields() {
        // Arrange
        TestSQSMessage message = new TestSQSMessage();
        String messageId = UUID.randomUUID().toString();
        String eventType = "UPDATED_EVENT";
        Instant timestamp = Instant.now();
        
        // Act
        message.setMessageId(messageId);
        message.setEventType(eventType);
        message.setTimestamp(timestamp);
        
        // Assert
        assertEquals(messageId, message.getMessageId());
        assertEquals(eventType, message.getEventType());
        assertEquals(timestamp, message.getTimestamp());
    }
    
    // Test implementation of SQSMessage
    private static class TestSQSMessage extends SQSMessage {
        public TestSQSMessage() {
            super();
        }
        
        public TestSQSMessage(String eventType) {
            super(eventType);
        }
        
        public TestSQSMessage(String messageId, String eventType, Instant timestamp) {
            super(messageId, eventType, timestamp);
        }
    }
}
