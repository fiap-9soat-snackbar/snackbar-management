# IAM Module Clean Architecture Changes

This document outlines the necessary changes to implement a clean architecture structure in the IAM module, based on the product module's implementation.

## Current Structure vs. Clean Architecture

The product module follows a clean hexagonal architecture with clear separation of concerns:

- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases, ports (interfaces), and gateways (interfaces)
- **Infrastructure Layer**: Controllers, repositories, and external services

The IAM module currently has a more traditional layered architecture that needs to be refactored to match the clean architecture pattern.

## Required Changes

### 1. Domain Layer

#### 1.1. Entity Consolidation
- Merge `UserEntity` and `UserDetailsEntity` into a single domain entity
- Implement proper validation in the domain entity constructor
- Move business rules into the domain entity

```java
// Example structure
package com.snackbar.iam.domain.entity;

public class User {
    private String id;
    private String name;
    private String email;
    private String cpf;
    private IamRole role;
    private String password;
    
    // Constructor with validation
    public User(String id, String name, String email, String cpf, IamRole role, String password) {
        validateUser(name, email, cpf, role, password);
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.role = role;
        this.password = password;
    }
    
    // Business rules for user validation
    private static void validateUser(String name, String email, String cpf, IamRole role, String password) {
        // Validation logic here
    }
}
```

#### 1.2. Domain Exceptions
- Create specific domain exceptions in `com.snackbar.iam.domain.exceptions`
  - `UserNotFoundException`
  - `InvalidCredentialsException`
  - `DuplicateUserException`
  - `InvalidUserDataException`

#### 1.3. Domain Events
- Create domain events for user lifecycle
  - `UserCreatedEvent`
  - `UserUpdatedEvent`
  - `UserDeletedEvent`

### 2. Application Layer

#### 2.1. Ports (Interfaces)
- Create input ports (use case interfaces) in `com.snackbar.iam.application.ports.in`
  - `RegisterUserInputPort`
  - `AuthenticateUserInputPort`
  - `GetUserByCpfInputPort`
  - `GetAllUsersInputPort`
  - `DeleteUserInputPort`

```java
// Example input port
package com.snackbar.iam.application.ports.in;

import com.snackbar.iam.domain.entity.User;

public interface RegisterUserInputPort {
    User registerUser(User user);
}
```

- Create output ports in `com.snackbar.iam.application.ports.out`
  - `DomainEventPublisher`

#### 2.2. Gateways (Repository Interfaces)
- Create gateway interfaces in `com.snackbar.iam.application.gateways`
  - `UserGateway`

```java
// Example gateway interface
package com.snackbar.iam.application.gateways;

import com.snackbar.iam.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserGateway {
    User createUser(User user);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(String id);
}
```

#### 2.3. Use Cases
- Create use case implementations in `com.snackbar.iam.application.usecases`
  - `RegisterUserUseCase`
  - `AuthenticateUserUseCase`
  - `GetUserByCpfUseCase`
  - `GetAllUsersUseCase`
  - `DeleteUserUseCase`

```java
// Example use case implementation
package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.RegisterUserInputPort;
import com.snackbar.iam.application.ports.out.DomainEventPublisher;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserCreatedEvent;
import com.snackbar.iam.domain.exceptions.DuplicateUserException;

public class RegisterUserUseCase implements RegisterUserInputPort {
    
    private final UserGateway userGateway;
    private final DomainEventPublisher eventPublisher;
    
    public RegisterUserUseCase(UserGateway userGateway, DomainEventPublisher eventPublisher) {
        this.userGateway = userGateway;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public User registerUser(User user) {
        // Check if user already exists
        userGateway.findByCpf(user.getCpf()).ifPresent(u -> {
            throw new DuplicateUserException("User with CPF " + user.getCpf() + " already exists");
        });
        
        userGateway.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new DuplicateUserException("User with email " + user.getEmail() + " already exists");
        });
        
        // Create user
        User createdUser = userGateway.createUser(user);
        
        // Publish domain event
        eventPublisher.publish(new UserCreatedEvent(createdUser));
        
        return createdUser;
    }
}
```

### 3. Infrastructure Layer

#### 3.1. Controllers
- Refactor controllers in `com.snackbar.iam.infrastructure.controllers`
  - `AuthenticationController`
  - `UserController`
- Create DTOs in `com.snackbar.iam.infrastructure.controllers.dto`
  - Request DTOs
  - Response DTOs
- Create DTO mappers in `com.snackbar.iam.infrastructure.controllers`
  - `UserDTOMapper`

```java
// Example controller
package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.application.usecases.*;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.controllers.dto.ResponseDTO;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByCpfUseCase getUserByCpfUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UserDTOMapper userDTOMapper;
    
    // Constructor
    
    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody RegisterUserRequestDTO requestDTO) {
        User user = userDTOMapper.toEntity(requestDTO);
        User createdUser = registerUserUseCase.registerUser(user);
        return ResponseEntity.ok(new ResponseDTO(true, "User registered successfully", userDTOMapper.toDTO(createdUser)));
    }
    
    // Other endpoints
}
```

#### 3.2. Repository Implementation
- Create persistence entities in `com.snackbar.iam.infrastructure.persistence`
  - `UserEntity`
- Create repository interfaces in `com.snackbar.iam.infrastructure.persistence`
  - `UserRepository`
- Create gateway implementations in `com.snackbar.iam.infrastructure.gateways`
  - `UserRepositoryGateway`
  - `UserEntityMapper`

```java
// Example repository gateway
package com.snackbar.iam.infrastructure.gateways;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import com.snackbar.iam.infrastructure.persistence.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepositoryGateway implements UserGateway {
    
    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    
    public UserRepositoryGateway(UserRepository userRepository, UserEntityMapper userEntityMapper) {
        this.userRepository = userRepository;
        this.userEntityMapper = userEntityMapper;
    }
    
    @Override
    public User createUser(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(userEntity);
        return userEntityMapper.toDomainObj(savedEntity);
    }
    
    // Other methods
}
```

#### 3.3. Security Implementation
- Move security configuration to `com.snackbar.iam.infrastructure.config`
  - `SecurityConfig`
  - `JwtConfig`
- Create security services in `com.snackbar.iam.infrastructure.security`
  - `JwtService`
  - `JwtAuthenticationFilter`

#### 3.4. Configuration
- Create configuration classes in `com.snackbar.iam.infrastructure.config`
  - `IamConfig` (for dependency injection)

```java
// Example configuration
package com.snackbar.iam.infrastructure.config;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.out.DomainEventPublisher;
import com.snackbar.iam.application.usecases.*;
import com.snackbar.iam.infrastructure.controllers.UserDTOMapper;
import com.snackbar.iam.infrastructure.gateways.UserEntityMapper;
import com.snackbar.iam.infrastructure.gateways.UserRepositoryGateway;
import com.snackbar.iam.infrastructure.persistence.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamConfig {
    
    @Bean
    UserGateway userGateway(UserRepository userRepository, UserEntityMapper userEntityMapper) {
        return new UserRepositoryGateway(userRepository, userEntityMapper);
    }
    
    @Bean
    RegisterUserUseCase registerUserUseCase(UserGateway userGateway, DomainEventPublisher eventPublisher) {
        return new RegisterUserUseCase(userGateway, eventPublisher);
    }
    
    // Other beans
}
```

## Implementation Strategy

1. Create the domain layer first
   - Domain entities with validation
   - Domain exceptions
   - Domain events

2. Create the application layer
   - Define ports (interfaces)
   - Define gateways (interfaces)
   - Implement use cases

3. Create the infrastructure layer
   - Implement controllers and DTOs
   - Implement repository and gateway implementations
   - Configure security components
   - Create configuration classes

4. Migrate existing functionality incrementally
   - Start with user registration and authentication
   - Then implement user retrieval
   - Finally implement user deletion

5. Add comprehensive tests at each layer
   - Domain entity tests
   - Use case tests
   - Controller tests
   - Integration tests
