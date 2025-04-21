# AWS SQS Integration for Snackbar Management System

This project demonstrates the integration of AWS SQS (Simple Queue Service) with a Spring Boot application for asynchronous messaging.

## Overview

The Snackbar Management System uses AWS SQS to publish domain events when products are created, updated, or deleted. This enables other services to react to these events asynchronously.

## Implementation Details

### Key Components

1. **SQSDomainEventPublisher**: Implements the `DomainEventPublisher` interface to publish domain events to AWS SQS.
   - Supports multiple environments through Spring profiles (`dev` and `aws-local`)
   - Uses different queue URLs based on the active profile
   - Handles error cases gracefully

2. **SQSMessageProducer**: Responsible for sending messages to SQS queues.
   - Abstracts the AWS SDK interaction
   - Provides logging for debugging

3. **ProductMessageMapper**: Maps domain events to SQS message format.
   - Converts domain objects to a format suitable for messaging

### Configuration

The system supports two environments:

1. **dev**: Uses LocalStack for local development
   - Queue URL: `http://localstack:4566/000000000000/product-events`
   - Configured in `application-dev.properties`

2. **aws-local**: Uses real AWS services but runs locally
   - Queue URL from environment variable: `${AWS_SQS_PRODUCT_EVENTS_QUEUE_URL}`
   - Configured in `application-aws-local.properties`

### Message Format

Messages are sent in JSON format with the following structure:

```json
{
  "messageId": "uuid-string",
  "eventType": "PRODUCT_CREATED|PRODUCT_UPDATED|PRODUCT_DELETED",
  "timestamp": 1745202664.371865219,
  "productId": "product-id",
  "name": "Product Name",
  "category": "Category",
  "description": "Description",
  "price": 9.99,
  "cookingTime": 5
}
```

## Testing

The system includes comprehensive tests:

1. **Unit Tests**: Test individual components in isolation
   - `SQSDomainEventPublisherTest`: Tests the publisher with mocked dependencies
   - `ProductMessageMapperTest`: Tests the mapping logic

2. **Integration Tests**: Test the integration with AWS SQS
   - `test_sqs.sh`: Script to test the full flow of creating, updating, and deleting products

## Running the Application

### Prerequisites

- Docker and Docker Compose
- AWS CLI (for testing with real AWS)
- Java 21
- jq (for parsing JSON responses)

### Local Development with LocalStack

1. Run the test script with the `dev` environment:
   ```
   ./test_sqs.sh dev
   ```

   Or run it interactively and select option 1:
   ```
   ./test_sqs.sh
   ```

2. This will:
   - Build the application (skipping tests)
   - Start the containers using docker-compose.yml and docker-compose.localstack.yml
   - Create an SQS queue in LocalStack
   - Test the SQS integration by creating, updating, and deleting a product
   - Show the messages in the LocalStack SQS queue

### AWS Integration

1. Ensure you have valid AWS credentials in ~/.aws/credentials

2. Run the test script with the `aws-local` environment:
   ```
   ./test_sqs.sh aws-local
   ```

   Or run it interactively and select option 2:
   ```
   ./test_sqs.sh
   ```

3. This will:
   - Build the application (skipping tests)
   - Start the containers using docker-compose.yml and docker-compose.aws.yml
   - Mount your AWS credentials into the container
   - Test the SQS integration by creating, updating, and deleting a product
   - Show the SQS-related logs from the application

## Troubleshooting

Common issues and solutions:

1. **Connection refused to LocalStack**: Ensure LocalStack container is running and healthy
2. **AWS credentials not found**: Check that credentials are properly mounted in the container
3. **Queue not found**: Verify the queue has been created in SQS/LocalStack
4. **Message serialization errors**: Ensure the JavaTimeModule is registered with ObjectMapper
5. **Missing environment variables**: Both AWS and dev properties are required in the application configuration, even if only one profile is active
