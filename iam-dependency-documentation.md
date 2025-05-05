# IAM Module Dependencies Documentation

This document maps the dependencies between components in the IAM module, identifying all integration points between legacy and clean architecture components. This documentation is part of the Phase 3 migration plan for legacy components.

## Core Dependencies

### External Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.4.1 | Core framework |
| Spring Security | 5.8.4 | Authentication and authorization |
| JWT | 0.11.5 | Token-based authentication |
| MongoDB | via Spring Data | User data persistence |
| Lombok | 1.18.34 | Boilerplate code reduction |
| Spring Cloud OpenFeign | 4.2.0 | HTTP client for external services |
| SpringDoc OpenAPI | 2.6.0 | API documentation |
| AWS SDK | 2.31.25 | AWS service integration (SQS) |
| Jackson | bundled | JSON serialization/deserialization |
| Mockito | 5.11.0 | Testing framework |

### Internal Module Dependencies

#### Domain Layer Dependencies
The domain layer is the core of the clean architecture and should have no dependencies on other layers.

| Component | Dependencies | Notes |
|-----------|--------------|-------|
| User Entity | None | Core domain entity |
| Domain Exceptions | None | Domain-specific exceptions |
| Domain Events | None | Domain events for state changes |

#### Application Layer Dependencies

| Component | Dependencies | Notes |
|-----------|--------------|-------|
| Input Ports | Domain Layer | Interfaces for use cases |
| Use Cases | Domain Layer, Gateways | Business logic implementation |
| Gateways | Domain Layer | Interfaces for infrastructure services |
| Event Publishers | Domain Layer | Interfaces for event publishing |

#### Infrastructure Layer Dependencies

| Component | Dependencies | Notes |
|-----------|--------------|-------|
| Controllers | Application Layer (Input Ports), DTOs | REST API endpoints |
| DTOs | None | Data transfer objects |
| Repositories | Domain Layer, Spring Data | Data access |
| Security | Spring Security, JWT | Authentication and authorization |
| Adapters | Application Layer, Legacy Components | Bridge between clean architecture and legacy code |

## Legacy Component Integration Points

### Authentication Flow

1. **UserAuthController** → **AuthenticateUserInputPort** → **AuthenticateUserUseCase**
   - Legacy integration: **UserDetailsServiceAdapter** adapts between domain User and Spring Security UserDetails

2. **JwtAuthenticationFilter** → **JwtService** → **UserDetailsServiceAdapter**
   - Legacy integration: Filter extracts JWT token and uses adapter to load user details

### User Management Flow

1. **UserMgmtController** → **Various Input Ports** → **Corresponding Use Cases**
   - Legacy integration: **UserRepositoryGateway** adapts between domain User and persistence UserEntity

2. **UserRepositoryGateway** → **UserRepository** → **MongoDB**
   - Legacy integration: **UserEntityMapper** converts between domain and persistence models

## Cross-Cutting Dependencies

### Security Configuration

1. **IamSecurityConfig** configures Spring Security
   - Dependencies: **JwtAuthenticationFilter**, **UserDetailsServiceAdapter**

2. **JwtService** handles JWT operations
   - Dependencies: Environment variables for configuration

### Error Handling

1. **IamGlobalExceptionHandler** handles exceptions across the module
   - Dependencies: Domain exceptions, Spring Web

## Configuration Dependencies

| Configuration | Source | Used By | Notes |
|---------------|--------|---------|-------|
| JWT Secret | Environment Variable | JwtService | Required for token signing/validation |
| JWT Expiration | Environment Variable | JwtService | Token lifetime |
| Password Policy | Environment Variable | User validation | Password requirements |
| CORS Settings | Environment Variable | Security config | Cross-origin resource sharing |
| OpenAPI Settings | Environment Variable | API documentation | Documentation configuration |

## Event Dependencies

| Event | Publisher | Subscribers | Notes |
|-------|-----------|-------------|-------|
| UserCreatedEvent | RegisterUserUseCase | Not fully implemented | Notification of user creation |
| UserUpdatedEvent | Not fully implemented | Not fully implemented | Notification of user updates |
| UserDeletedEvent | DeleteUserUseCase | Not fully implemented | Notification of user deletion |

## Removal Dependencies

For each legacy component, the following dependencies must be addressed before removal:

### UserDetailsEntity
- Replace with **UserDetailsAdapter** in all security contexts
- Ensure **User** domain entity contains all necessary information

### IamService/IamServiceImpl
- Migrate all functionality to appropriate use cases
- Update all controllers to use input ports instead

### Direct Repository Usage
- Ensure all data access goes through gateways
- Update all service classes to use gateways

## Testing Dependencies

| Test Type | Dependencies | Notes |
|-----------|--------------|-------|
| Unit Tests | JUnit, Mockito | Test individual components in isolation |
| Integration Tests | Spring Test, MongoDB Test Containers | Test component interactions |
| Security Tests | Spring Security Test | Test authentication and authorization |

## Next Steps

1. Complete the implementation of event publishers and subscribers
2. Finalize the adapter components between new and legacy code
3. Implement comprehensive tests for all components
4. Document removal criteria for each legacy component
5. Establish a timeline for completing the migration
