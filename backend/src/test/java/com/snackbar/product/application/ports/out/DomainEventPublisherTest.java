package com.snackbar.product.application.ports.out;

import com.snackbar.product.domain.event.DomainEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventPublisherTest {

    // Test implementation of DomainEventPublisher for testing
    private static class TestDomainEventPublisher implements DomainEventPublisher {
        private DomainEvent lastPublishedEvent;
        private int publishCount = 0;
        
        @Override
        public void publish(DomainEvent event) {
            this.lastPublishedEvent = event;
            this.publishCount++;
        }
        
        public DomainEvent getLastPublishedEvent() {
            return lastPublishedEvent;
        }
        
        public int getPublishCount() {
            return publishCount;
        }
    }
    
    // Test implementation of DomainEvent for testing
    private static class TestDomainEvent extends DomainEvent {
        private final String testData;
        
        public TestDomainEvent(String testData) {
            this.testData = testData;
        }
        
        public String getTestData() {
            return testData;
        }
    }

    @Test
    void shouldPublishEvent() {
        // Arrange
        TestDomainEventPublisher publisher = new TestDomainEventPublisher();
        TestDomainEvent event = new TestDomainEvent("test-data");
        
        // Act
        publisher.publish(event);
        
        // Assert
        assertEquals(1, publisher.getPublishCount());
        assertSame(event, publisher.getLastPublishedEvent());
        assertEquals("test-data", ((TestDomainEvent)publisher.getLastPublishedEvent()).getTestData());
    }
    
    @Test
    void shouldPublishMultipleEvents() {
        // Arrange
        TestDomainEventPublisher publisher = new TestDomainEventPublisher();
        TestDomainEvent event1 = new TestDomainEvent("test-data-1");
        TestDomainEvent event2 = new TestDomainEvent("test-data-2");
        
        // Act
        publisher.publish(event1);
        publisher.publish(event2);
        
        // Assert
        assertEquals(2, publisher.getPublishCount());
        assertSame(event2, publisher.getLastPublishedEvent());
        assertEquals("test-data-2", ((TestDomainEvent)publisher.getLastPublishedEvent()).getTestData());
    }
}
