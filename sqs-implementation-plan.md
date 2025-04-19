# SQS Implementation Plan for Snackbar Management

This document outlines the step-by-step implementation plan for adding Amazon SQS-based asynchronous communication to the Snackbar Management application, following Clean Architecture principles.

## Implementation Steps

### 1. Domain Layer: Domain Events (Core)

Create domain event classes to represent significant state changes:
- `DomainEvent` (base abstract class)
- `ProductCreatedEvent`
- `ProductUpdatedEvent`
- `ProductDeletedEvent`

These events will be raised by the domain layer and eventually published to SQS.

### 2. Application Layer: Use Case Interfaces (Input Ports)

Create interfaces for use cases to properly implement dependency inversion:
- `CreateProductInputPort`
- `GetProductByIdInputPort`
- `UpdateProductByIdInputPort`
- `DeleteProductByIdInputPort`
- etc.

### 3. Application Layer: Event Publisher Port (Output Port)

Create an interface for publishing domain events:
- `DomainEventPublisher`

This interface will be implemented by the infrastructure layer to publish events to SQS.

### 4. Application Layer: Updated Use Cases

Update existing use cases to:
- Implement the new input port interfaces
- Publish domain events when appropriate
- Maintain existing functionality

### 5. Infrastructure Layer: Message Models

Create message models for SQS communication:
- `ProductMessage`

These models will be used to serialize/deserialize messages sent to/from SQS.

### 6. Infrastructure Layer: Event Publisher Implementation

Implement the `DomainEventPublisher` interface with SQS:
- `SQSDomainEventPublisher`

This class will convert domain events to SQS messages and publish them.

### 7. Infrastructure Layer: Message Mapper

Create a mapper to convert between domain objects and message models:
- `ProductMessageMapper`

This will handle the translation between domain events and SQS messages.

### 8. Infrastructure Layer: SQS Client Configuration

Configure the AWS SQS client:
- `SQSConfig`

This will set up the connection to AWS SQS using the AWS SDK.

### 9. Infrastructure Layer: Message Consumer

Create a consumer to process incoming SQS messages:
- `SQSProductMessageConsumer`

This will listen for messages and invoke the appropriate use cases.

### 10. Infrastructure Layer: Extended Message Mapper

Add methods to the message mapper to convert from messages to domain objects:
- Additional methods in `ProductMessageMapper`

### 11. Application Properties

Update application properties with SQS configuration:
- Queue URLs
- AWS region
- Other SQS-specific settings

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
