package com.snackbar.infrastructure.messaging.sqs.mapper;

import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageMapperTest {

    @Test
    void testMessageMapperInterface() {
        // This is a test to verify the interface contract
        // Create a test implementation of MessageMapper
        TestMessageMapper mapper = new TestMessageMapper();
        
        // Create a test domain object
        TestDomainObject domainObject = new TestDomainObject("test-id", "Test Name");
        
        // Test toMessage method
        TestSQSMessage message = mapper.toMessage(domainObject, "TEST_EVENT");
        assertNotNull(message);
        assertEquals("test-id", message.getDomainId());
        assertEquals("Test Name", message.getDomainName());
        assertEquals("TEST_EVENT", message.getEventType());
        
        // Test toDomainObject method
        TestDomainObject convertedObject = mapper.toDomainObject(message);
        assertNotNull(convertedObject);
        assertEquals("test-id", convertedObject.getId());
        assertEquals("Test Name", convertedObject.getName());
    }
    
    // Test implementation of MessageMapper
    private static class TestMessageMapper implements MessageMapper<TestDomainObject, TestSQSMessage> {
        @Override
        public TestSQSMessage toMessage(TestDomainObject domainObject, String eventType) {
            TestSQSMessage message = new TestSQSMessage();
            message.setEventType(eventType);
            message.setDomainId(domainObject.getId());
            message.setDomainName(domainObject.getName());
            return message;
        }
        
        @Override
        public TestDomainObject toDomainObject(TestSQSMessage message) {
            return new TestDomainObject(message.getDomainId(), message.getDomainName());
        }
    }
    
    // Test domain object
    private static class TestDomainObject {
        private final String id;
        private final String name;
        
        public TestDomainObject(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
    }
    
    // Test SQS message
    private static class TestSQSMessage extends SQSMessage {
        private String domainId;
        private String domainName;
        
        public TestSQSMessage() {
            super();
        }
        
        public String getDomainId() {
            return domainId;
        }
        
        public void setDomainId(String domainId) {
            this.domainId = domainId;
        }
        
        public String getDomainName() {
            return domainName;
        }
        
        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }
    }
}
