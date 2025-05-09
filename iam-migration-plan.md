# IAM Module Legacy Code Removal Plan

This document outlines a detailed plan for removing legacy code from the IAM module and completing the migration to a clean architecture structure that aligns with the product module. The plan is based on the current state of the codebase, existing adapters, and dependencies between layers.

## Current State Analysis

The IAM module is currently in a hybrid state with:

1. **Clean Architecture Components** (already implemented):
   - Domain entities (`User.java`)
   - Domain events (`UserCreatedEvent.java`, `UserUpdatedEvent.java`, `UserDeletedEvent.java`)
   - Input/output ports
   - Use cases
   - Gateway interfaces
   - Infrastructure implementations including:
     - Controllers (`UserAuthController.java`, `UserMgmtController.java`)
     - DTOs in infrastructure.controllers.dto
     - Security components (`IamJwtAuthenticationFilter.java`, `JwtService.java`)
     - Configuration (`IamSecurityConfig.java`, `IamAuthenticationConfig.java`)
     - Persistence (`UserEntity.java`, `UserRepository.java` in infrastructure.persistence)

2. **Legacy Components** (to be removed):
   - ✅ `web` package with controllers (REMOVED)
   - ✅ `web` package with DTOs (REMOVED)
   - `UserEntity.java` in domain layer
   - ✅ `UserDetailsEntity.java` in domain layer (REMOVED)
   - ✅ `AuthenticationService.java` in application layer (REMOVED)
   - ✅ `UserService.java` in application layer (REMOVED)
   - ✅ `JwtService.java` in application layer (REMOVED)
   - `SecurityConfiguration.java` and `SecurityConfigurationAdapter.java` in application layer
   - `JwtAuthenticationFilter.java` in application layer
   - ✅ `IamRepository.java` in infrastructure root (REMOVED)
   - ✅ `UserRepository.java` in infrastructure root (REMOVED)

3. **Adapter Components** (temporary):
   - `UserEntityAdapter.java` in domain.adapter
   - `AuthenticationServiceAdapter.java` and `UserServiceAdapter.java` in application.adapter
   - `UserRepositoryAdapter.java`, `IamRepositoryAdapter.java`, `UserDetailsServiceAdapter.java` in infrastructure.adapter

## Target Package Structure

The target structure is already largely implemented, with the following components in place:

```
com.snackbar.iam
├── domain
│   ├── entity
│   │   └── User.java
│   ├── exceptions
│   │   ├── UserNotFoundException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── DuplicateUserException.java
│   │   └── InvalidUserDataException.java
│   └── event
│       ├── UserCreatedEvent.java
│       ├── UserUpdatedEvent.java
│       ├── UserDeletedEvent.java
│       └── UserDomainEvent.java
├── application
│   ├── ports
│   │   ├── in
│   │   │   ├── RegisterUserInputPort.java
│   │   │   ├── AuthenticateUserInputPort.java
│   │   │   ├── GetUserByCpfInputPort.java
│   │   │   ├── GetAllUsersInputPort.java
│   │   │   └── DeleteUserInputPort.java
│   │   └── out
│   │       └── IamDomainEventPublisher.java
│   ├── gateways
│   │   └── UserGateway.java
│   └── usecases
│       ├── RegisterUserUseCase.java
│       ├── AuthenticateUserUseCase.java
│       ├── GetUserByCpfUseCase.java
│       ├── GetAllUsersUseCase.java
│       └── DeleteUserUseCase.java
└── infrastructure
    ├── controllers
    │   ├── UserAuthController.java
    │   ├── UserMgmtController.java
    │   ├── IamGlobalExceptionHandler.java
    │   ├── UserDTOMapper.java
    │   └── dto
    │       ├── RegisterUserRequestDTO.java
    │       ├── LoginRequestDTO.java
    │       ├── AuthenticationResponseDTO.java
    │       ├── UpdateUserRequestDTO.java
    │       ├── UserResponseDTO.java
    │       └── IamErrorResponseDTO.java
    ├── persistence
    │   ├── UserEntity.java
    │   └── UserRepository.java
    ├── gateways
    │   ├── UserRepositoryGateway.java
    │   └── UserEntityMapper.java
    ├── security
    │   ├── JwtService.java
    │   ├── UserDetailsAdapter.java
    │   ├── IamJwtAuthenticationFilter.java
    │   └── exception
    │       └── [Security exceptions]
    └── config
        ├── IamConfig.java
        ├── IamSecurityConfig.java
        └── IamAuthenticationConfig.java
```

## Migration Strategy

The migration will follow the natural dependency direction (from outer layers to inner layers) while leveraging the adapter pattern to maintain functionality throughout the process:

1. **Start with Web Layer**: ✅ Remove legacy controllers and DTOs (COMPLETED)
2. **Application Layer**: ✅ Remove legacy services and security components (COMPLETED)
3. **Infrastructure Layer**: ✅ Remove legacy repositories and infrastructure components (COMPLETED)
4. **Domain Layer**: Remove legacy domain entities
5. **Remove Adapters**: Once all legacy components are removed, remove the adapter components

After each step, we will run `iam_test_integration_v3.sh` to verify that everything still works correctly.

## Detailed Migration Steps

### Phase 1: Remove Web Layer Legacy Components ✅

#### Step 1: Remove Legacy Web Controllers ✅

1. **Verify Replacement Controllers** ✅
   - Ensure `UserAuthController.java` covers all functionality of `AuthenticationController.java`
   - Ensure `UserMgmtController.java` covers all functionality of `UserController.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Controllers** ✅
   - Remove `AuthenticationController.java`
   - Remove `UserController.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove Legacy DTOs ✅

1. **Verify Replacement DTOs** ✅
   - Ensure all DTOs in `web.dto` package have equivalents in `infrastructure.controllers.dto`
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy DTOs** ✅
   - Remove all DTOs in `web.dto` package
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove Web Package ✅

1. **Remove Web Package** ✅
   - After all controllers and DTOs are removed, remove the entire `web` package
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 2: Remove Application Layer Legacy Components ✅

#### Step 1: Remove JwtService.java ✅

1. **Update JwtAuthenticationFilter** ✅
   - Modify `JwtAuthenticationFilter` to use the new `JwtService` directly
   - Ensure proper qualifiers are used
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy JwtService** ✅
   - Remove `JwtService.java` from the application layer
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove AuthenticationService.java ✅

1. **Update References** ✅
   - Identify any remaining references to `AuthenticationService`
   - Update them to use `AuthenticationServiceAdapter` directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Service** ✅
   - Remove `AuthenticationService.java`
   - Update `AuthenticationServiceAdapter` to implement necessary interfaces directly
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove UserService.java ✅

1. **Update References** ✅
   - Identify any remaining references to `UserService`
   - Update them to use `UserServiceAdapter` directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Service** ✅
   - Remove `UserService.java`
   - Update `UserServiceAdapter` to implement necessary interfaces directly
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 3: Remove Infrastructure Layer Legacy Components ✅

#### Step 1: Remove IamRepository.java ✅

1. **Update ApplicationConfiguration** ✅
   - Modify `ApplicationConfiguration` to use the new repository interfaces
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Update References** ✅
   - Identify any remaining references to `IamRepository`
   - Update them to use `IamRepositoryAdapter` or the new repositories directly
   - Run `iam_test_integration_v3.sh` to verify functionality

3. **Remove Legacy Repository** ✅
   - Remove `IamRepository.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove UserRepository.java ✅

1. **Update References** ✅
   - Identify any remaining references to `UserRepository`
   - Update them to use `UserRepositoryAdapter` or the new repositories directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Repository** ✅
   - Remove `UserRepository.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 4: Remove Domain Layer Legacy Components

#### Step 1: Remove UserDetailsEntity.java ✅

1. **Update ApplicationConfiguration** ✅
   - Modify `ApplicationConfiguration` to use the new domain entities
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Update References** ✅
   - Identify any remaining references to `UserDetailsEntity`
   - Update them to use `UserEntityAdapter` or the new domain entities directly
   - Run `iam_test_integration_v3.sh` to verify functionality

3. **Remove Legacy Entity** ✅
   - Remove `UserDetailsEntity.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove UserEntity.java ✅

1. **Update References** ✅
   - Identify any remaining references to `UserEntity`
   - Update them to use `UserEntityAdapter` or the new domain entities directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Legacy Entity** ✅
   - Remove `UserEntity.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

### Phase 5: Remove Adapter Components

#### Step 1: Remove Domain Layer Adapters

1. **Update References to UserEntityAdapter** ✅
   - Identify any remaining references to `UserEntityAdapter`
   - Update them to use the new domain entities directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Domain Adapters**
   - Remove `UserEntityAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 2: Remove Infrastructure Layer Adapters

1. **Update References to Repository Adapters**
   - Update all references to `IamRepositoryAdapter` and `UserRepositoryAdapter` to use the new repositories directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Infrastructure Adapters**
   - Remove `IamRepositoryAdapter.java`
   - Remove `UserRepositoryAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

#### Step 3: Remove Application Layer Adapters

1. **Update References to Service Adapters**
   - Update all references to `AuthenticationServiceAdapter` and `UserServiceAdapter` to use the use cases directly
   - Run `iam_test_integration_v3.sh` to verify functionality

2. **Remove Application Adapters**
   - Remove `AuthenticationServiceAdapter.java`
   - Remove `UserServiceAdapter.java`
   - Run `iam_test_integration_v3.sh` to verify functionality

## Progress Tracking

### Completed Steps
- ✅ Updated controller mappings from `/api/v2/user/*` to `/api/user/*`
- ✅ Removed legacy web controllers (`AuthenticationController.java`, `UserController.java`)
- ✅ Removed legacy DTOs in web.dto package
- ✅ Removed entire web package
- ✅ Updated test script to use new endpoint paths
- ✅ Verified functionality with integration tests
- ✅ Updated adapter classes to use new DTOs from `infrastructure.controllers.dto`
- ✅ Updated service classes to use new DTOs
- ✅ Added explicit bean names to adapter components
- ✅ Added explicit qualifiers to repository components
- ✅ Added explicit bean names and qualifiers to security components
- ✅ Added explicit bean names and qualifiers to application service components
- ✅ Verified functionality with integration tests after application service component updates
- ✅ Completed comprehensive dependency audit
- ✅ Removed JwtService.java from application layer
- ✅ Removed AuthenticationService.java from application layer
- ✅ Removed UserService.java from application layer
- ✅ Removed IamRepository.java from infrastructure layer
- ✅ Removed UserRepository.java from infrastructure layer
- ✅ Modified adapter classes to no longer implement legacy interfaces
- ✅ Verified functionality with integration tests after removing legacy repositories
- ✅ Removed UserDetailsEntity.java from domain layer
- ✅ Updated AuthenticationServiceAdapter to use UserDetailsAdapter instead of UserDetailsEntity
- ✅ Updated ApplicationConfiguration to use domain entities directly
- ✅ Verified functionality with integration tests after removing UserDetailsEntity
- ✅ Removed UserEntity.java from domain layer
- ✅ Updated adapter classes to use infrastructure.persistence.UserEntity instead of domain.UserEntity
- ✅ Verified functionality with integration tests after removing UserEntity
- ✅ Updated adapter classes to use UserEntityMapper instead of UserEntityAdapter
- ✅ Removed UserEntityAdapter.java from domain layer
- ✅ Verified functionality with integration tests after removing UserEntityAdapter

### Current Work
- Working on removing infrastructure layer adapters

### Next Steps
- Update references to Repository Adapters
- Remove Repository Adapters
- Continue with application layer adapter removal

## Conclusion

This migration plan provides a detailed roadmap for removing legacy code from the IAM module and completing the migration to a clean architecture structure. By following the natural dependency direction (from outer layers to inner layers) while leveraging the adapter pattern, we can maintain functionality throughout the migration process and minimize the risk of breaking changes.

The plan acknowledges that many clean architecture components are already in place and focuses on removing legacy components and adapters in a systematic way. After each step, we will run `iam_test_integration_v3.sh` to verify that everything still works correctly, ensuring a smooth migration process.
