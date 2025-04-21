# SQS Implementation Plan for Snackbar Management

This document outlines the step-by-step implementation plan for adding Amazon SQS-based asynchronous communication to the Snackbar Management application, following Clean Architecture principles.

## Implementation Status (Updated April 20, 2025)

### Completed Steps ✅

### 1. Domain Layer: Domain Events (Core) ✅ COMPLETED

Create domain event classes to represent significant state changes:
- `DomainEvent` (base abstract class) ✅
- `ProductCreatedEvent` ✅
- `ProductUpdatedEvent` ✅
- `ProductDeletedEvent` ✅

These events are raised by the domain layer and published to SQS.

### 2. Application Layer: Use Case Interfaces (Input Ports) ✅ COMPLETED

Create interfaces for use cases to properly implement dependency inversion:
- `CreateProductInputPort` ✅
- `GetProductByIdInputPort` ✅
- `UpdateProductByIdInputPort` ✅
- `DeleteProductByIdInputPort` ✅
- etc. ✅

### 3. Application Layer: Event Publisher Port (Output Port) ✅ COMPLETED

Create an interface for publishing domain events:
- `DomainEventPublisher` ✅

This interface is implemented by the infrastructure layer to publish events to SQS.

### 4. Application Layer: Updated Use Cases ✅ COMPLETED

Update existing use cases to:
- Implement the new input port interfaces ✅
- Publish domain events when appropriate ✅
- Maintain existing functionality ✅

### 5. Infrastructure Layer: Message Models ✅ COMPLETED

Create message models for SQS communication:
- `ProductMessage` ✅

These models are used to serialize/deserialize messages sent to/from SQS.

### 6. Infrastructure Layer: Event Publisher Implementation ✅ COMPLETED

Implement the `DomainEventPublisher` interface with SQS:
- `SQSDomainEventPublisher` ✅
- `NoOpDomainEventPublisher` ✅ (For non-production environments)

These classes convert domain events to SQS messages and publish them.

### 7. Infrastructure Layer: Message Mapper ✅ COMPLETED

Create a mapper to convert between domain objects and message models:
- `ProductMessageMapper` ✅

This handles the translation between domain events and SQS messages.

### 8. Infrastructure Layer: SQS Client Configuration ✅ COMPLETED

Configure the AWS SQS client:
- `SQSConfig` ✅

This sets up the connection to AWS SQS using the AWS SDK.

### 15. Update pom.xml ✅ COMPLETED

Add AWS SDK dependencies to the pom.xml file:
- AWS SQS SDK ✅
- Jackson JSR-310 support for Java 8 date/time types ✅

### Pending Steps ❌

### 9. Infrastructure Layer: Message Consumer ❌ NOT STARTED

Create a consumer to process incoming SQS messages:
- `SQSProductMessageConsumer` ❌

This will listen for messages and invoke the appropriate use cases.

### 10. Infrastructure Layer: Extended Message Mapper ❌ NOT STARTED

Add methods to the message mapper to convert from messages to domain objects:
- Additional methods in `ProductMessageMapper` ❌

### 11. Application Properties ❌ PARTIALLY COMPLETED

Update application properties with SQS configuration:
- Queue URLs ❌ (Only in docker-compose.yml environment variables)
- AWS region ❌ (Only in docker-compose.yml environment variables)
- Other SQS-specific settings ❌

### 16. Update docker-compose.yml ✅ PARTIALLY COMPLETED

- AWS credentials are mounted ✅
- Environment variables for AWS configuration are partially set up ✅
- LocalStack service for local testing is not added ❌

### 17. Create LocalStack initialization script ❌ NOT STARTED

Create a file at `localstack-init/init-aws.sh` to initialize SQS queues.

### 18. Update full_test.sh ❌ NOT STARTED

Add SQS testing to the full_test.sh script.

### 19. Update app environment in docker-compose.yml ❌ PARTIALLY COMPLETED

Add AWS environment variables to the app service.

### 20. Create a .env file for local development ❌ PARTIALLY COMPLETED

Add AWS configuration to the .env file.

## Additional Implementation Tasks

### 12. Update SQSDomainEventPublisher.java ✅ COMPLETED

```java
package com.snackbar.product.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.event.DomainEvent;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/**
 * Implementation of DomainEventPublisher that publishes events to AWS SQS.
 */
@Component
@Profile("prod") // Only use this implementation in production profile
public class SQSDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSDomainEventPublisher.class);
    
    private final ProductMessageMapper messageMapper;
    private final ObjectMapper objectMapper;
    private final String queueUrl;
    private final SqsClient sqsClient;
    
    public SQSDomainEventPublisher(
            ProductMessageMapper messageMapper,
            ObjectMapper objectMapper,
            SqsClient sqsClient,
            @Value("${aws.sqs.product-events-queue-url:https://sqs.us-east-1.amazonaws.com/123456789012/product-events}") String queueUrl) {
        this.messageMapper = messageMapper;
        this.objectMapper = objectMapper;
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }
    
    @Override
    public void publish(DomainEvent event) {
        try {
            ProductMessage message = messageMapper.toMessage(event);
            String messageBody = objectMapper.writeValueAsString(message);
            
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
                
            sqsClient.sendMessage(sendMessageRequest);
            
            logger.info("Event published to SQS queue {}: {}", 
                    queueUrl, messageBody);
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event to JSON: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to serialize event to JSON", e);
        } catch (Exception e) {
            logger.error("Failed to publish event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
```

### 13. Update SQSConfig.java ✅ COMPLETED

```java
package com.snackbar.product.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.snackbar.product.infrastructure.messaging.ProductMessageMapper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import java.net.URI;

/**
 * Configuration for AWS SQS client.
 * This class sets up the connection to AWS SQS using the AWS SDK.
 */
@Configuration
public class SQSConfig {
    
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    @Value("${aws.endpoint.url:}")
    private String awsEndpointUrl;
    
    @Value("${aws.access.key:}")
    private String awsAccessKey;
    
    @Value("${aws.secret.key:}")
    private String awsSecretKey;
    
    /**
     * Creates a ProductMessageMapper bean.
     *
     * @return The configured ProductMessageMapper
     */
    @Bean
    public ProductMessageMapper productMessageMapper() {
        return new ProductMessageMapper();
    }
    
    /**
     * Creates an ObjectMapper bean for JSON serialization/deserialization.
     * Registers the JavaTimeModule to handle Java 8 date/time types like Instant.
     *
     * @return The configured ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    /**
     * Creates an SQS client bean for production environment.
     *
     * @return The configured SQS client
     */
    @Bean
    @Profile("prod")
    public SqsClient sqsClient() {
        SqsClient.Builder builder = SqsClient.builder()
            .region(Region.of(awsRegion));
            
        // If endpoint URL is provided, use it (for LocalStack)
        if (awsEndpointUrl != null && !awsEndpointUrl.isEmpty()) {
            builder.endpointOverride(URI.create(awsEndpointUrl));
        }
        
        // If credentials are provided, use them
        if (awsAccessKey != null && !awsAccessKey.isEmpty() && 
            awsSecretKey != null && !awsSecretKey.isEmpty()) {
            builder.credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
                )
            );
        }
        
        return builder.build();
    }
}
```

### 14. Update application.properties ❌ NOT STARTED

Add the following properties to the existing application.properties file:

```properties
# AWS Configuration (used only when spring.profiles.active=prod)
aws.region=us-east-1
aws.sqs.product-events-queue-url=https://sqs.us-east-1.amazonaws.com/123456789012/product-events
# For LocalStack, use: aws.endpoint.url=http://localstack:4566
aws.endpoint.url=
aws.access.key=
aws.secret.key=
```

### 15. Update pom.xml ✅ COMPLETED

Add AWS SDK dependencies to the pom.xml file:

```xml
<!-- AWS SDK for SQS -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sqs</artifactId>
    <version>2.20.26</version>
</dependency>
```

### 16. Update docker-compose.yml ✅ PARTIALLY COMPLETED

Add LocalStack service for local testing:

```yaml
services:
  # ... existing services ...
  
  localstack:
    image: localstack/localstack:latest
    container_name: localstack
    ports:
      - "4566:4566"
    environment:
      - SERVICES=sqs
      - DEFAULT_REGION=us-east-1
    volumes:
      - ./localstack-init:/docker-entrypoint-initdb.d
```

### 17. Create LocalStack initialization script ❌ NOT STARTED

Create a file at `localstack-init/init-aws.sh`:

```bash
#!/bin/bash
# Create SQS queue
awslocal sqs create-queue --queue-name product-events
echo "AWS resources initialized"
```

Make it executable:
```bash
chmod +x localstack-init/init-aws.sh
```

### 18. Update full_test.sh ❌ NOT STARTED

Add SQS testing to the full_test.sh script:

```bash
# If running with prod profile, test SQS
if [ "$SPRING_PROFILES_ACTIVE" = "prod" ]; then
  echo "=== Testing SQS Event Publishing ==="
  echo "10. Creating product to trigger event"
  EVENT_PRODUCT_ID=$(curl -s -X POST -H "Content-Type: application/json" \
    -d '{"name":"Event Test Product","category":"Teste","description":"Product to test event publishing","price":9.99,"cookingTime":1}' \
    http://localhost:8080/api/product | jq -r '.data.id')
  
  echo "11. Checking SQS queue for messages"
  aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/000000000000/product-events --max-number-of-messages 10 | jq
  
  echo "12. Cleaning up test product"
  curl -s -X DELETE "http://localhost:8080/api/product/id/$EVENT_PRODUCT_ID" | jq
fi
```

### 19. Update app environment in docker-compose.yml ✅ PARTIALLY COMPLETED

Add the following environment variables to the app service:

```yaml
services:
  app:
    # ... existing configuration ...
    environment:
      # ... existing environment variables ...
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-default}
      - AWS_ENDPOINT_URL=${AWS_ENDPOINT_URL:-http://localstack:4566}
      - AWS_REGION=${AWS_REGION:-us-east-1}
      - AWS_ACCESS_KEY=${AWS_ACCESS_KEY:-test}
      - AWS_SECRET_KEY=${AWS_SECRET_KEY:-test}
      - AWS_SQS_PRODUCT_EVENTS_QUEUE_URL=${AWS_SQS_QUEUE_URL:-http://localstack:4566/000000000000/product-events}
```

### 20. Create a .env file for local development ✅ PARTIALLY COMPLETED

```
# MongoDB Configuration
DB_HOST=mongodb
DB_PORT=27017
APP_DB=snackbar
MONGODB_USER=snackbaruser
MONGODB_PASSWORD=snack01
MONGODB_OPTIONS=authSource=admin
MONGO_INITDB_ROOT_USERNAME=mongodbadmin
MONGO_INITDB_ROOT_PASSWORD=admin
MONGO_INITDB_DATABASE=snackbar

# JWT Configuration
JWT_SECRET=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
JWT_EXPIRES=3600000

# AWS Configuration
SPRING_PROFILES_ACTIVE=default
AWS_ENDPOINT_URL=http://localstack:4566
AWS_REGION=us-east-1
AWS_ACCESS_KEY=test
AWS_SECRET_KEY=test
AWS_SQS_QUEUE_URL=http://localstack:4566/000000000000/product-events
```

### 21. Create a run-with-sqs.sh script ✅ COMPLETED

```bash
#!/bin/bash
set -e

echo "=== Cleaning up environment ==="
docker compose down -v --rmi all
rm -rf backend/target

echo "=== Building application ==="
mvn -f backend/pom.xml clean package

echo "=== Starting containers with SQS enabled ==="
SPRING_PROFILES_ACTIVE=prod docker compose up --build -d

echo "=== Waiting for application to start ==="
sleep 15

echo "=== Running full test script ==="
SPRING_PROFILES_ACTIVE=prod ./full_test.sh

echo "=== All tests completed ==="
```

Make it executable:
```bash
chmod +x run-with-sqs.sh
```

## Key Insights and Fixes

1. **Java Time Serialization**: The ObjectMapper in SQSConfig has been properly configured with JavaTimeModule to handle Java 8 date/time types like Instant.

2. **AWS Credentials**: AWS credentials are mounted from the host to the Docker container using volume mapping in docker-compose.yml.

3. **SQS Integration Testing**: A test_sqs.sh script has been created to test SQS event publishing, but it may require additional configuration to work properly.

## Summary of the Implementation

This implementation:

1. **Follows Clean Architecture**:
   - Domain layer contains pure domain events
   - Application layer defines ports (interfaces) for both input and output
   - Infrastructure layer contains all AWS SQS specific code

2. **Maintains Separation of Concerns**:
   - Domain events are separate from messaging infrastructure
   - Use cases are decoupled from event publishing details
   - Message mapping happens at the infrastructure boundary

3. **Works with Terraform-deployed Queues**:
   - Uses queue URLs from configuration rather than creating queues
   - Can be easily integrated with Terraform outputs

4. **Keeps Spring Framework at the Outer Layers**:
   - Spring annotations only in infrastructure components
   - Core domain and application layers remain framework-agnostic

5. **Provides Flexibility with Profiles**:
   - Uses Spring profiles to switch between real SQS and mock implementation
   - Default profile uses NoOpDomainEventPublisher for development and testing
   - Production profile uses SQSDomainEventPublisher for real SQS integration
   - No separate application-prod.properties file needed
