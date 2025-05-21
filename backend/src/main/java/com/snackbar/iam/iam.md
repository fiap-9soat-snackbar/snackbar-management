# IAM Module

The IAM (Identity and Access Management) module is responsible for managing users, authentication, and authorization in the snack bar system. It follows a clean architecture approach with distinct layers for domain, application, and infrastructure.

## Domain Layer

### Entities

#### User

The `User` entity represents a user in the system. It has the following attributes:

- `id`: Unique identifier for the user
- `email`: Email address of the user (must be valid format)
- `password`: Hashed password for authentication
- `cpf`: Brazilian CPF number (must be valid format)
- `role`: User role (must be one of: "ADMIN", "CONSUMER")
- `fullName`: Full name of the user

The entity includes validation logic to ensure all data meets business requirements.

### Exceptions

- `InvalidUserDataException`: Thrown when user data doesn't meet validation requirements
- `UserNotFoundException`: Thrown when a user cannot be found in the repository
- `AuthenticationException`: Thrown when authentication fails
- `UnauthorizedException`: Thrown when a user attempts an unauthorized operation

## Application Layer

### Use Cases

The application layer contains the following use cases:

1. `RegisterUserUseCase`: Registers a new user in the system
2. `AuthenticateUserUseCase`: Authenticates a user and generates a JWT token
3. `GetUserByCpfUseCase`: Retrieves a user by their CPF
4. `ListUsersUseCase`: Lists all users in the system
5. `DeleteUserByIdUseCase`: Deletes a user by their ID
6. `UpdateUserByIdUseCase`: Updates an existing user

## Infrastructure Layer

### Controllers

The `UserAuthController` exposes the following REST endpoints:

- `POST /api/user/auth/signup`: Register a new user
- `POST /api/user/auth/login`: Authenticate user and get JWT token
- `GET /api/user/`: List all users (admin only)
- `GET /api/user/cpf/{cpf}`: Get a user by CPF
- `PUT /api/user/{id}`: Update a user
- `DELETE /api/user/{id}`: Delete a user

### Security

- `JwtTokenProvider`: Generates and validates JWT tokens
- `SecurityConfig`: Spring Security configuration for authentication and authorization
- `UserDetailsServiceImpl`: Implementation of Spring Security's UserDetailsService

### Persistence

- `UserEntity`: MongoDB document representation of a user
- `UserRepository`: Spring Data MongoDB repository for user persistence
- `UserRepositoryGateway`: Implementation of the repository pattern for users

## Testing

The IAM module has comprehensive test coverage:

- **Domain Tests**: Coverage of the `User` entity, including all validation rules
- **Application Tests**: Tests for all use cases with mocked repositories
- **Integration Tests**: Tests for the repository gateway and controllers

### Test Coverage

The `User` entity has extensive test coverage, including:
- Successful user creation
- Email validation (null, empty, invalid format)
- Password validation (null, empty, too short, complexity requirements)
- CPF validation (null, empty, invalid format)
- Role validation (null, empty, invalid)
- Full name validation (null, empty)

## Running Tests

To run the tests for the IAM module:

```bash
mvn -f backend/pom.xml test
```

To generate a test coverage report:

```bash
mvn -f backend/pom.xml clean test jacoco:report
```

The coverage report will be available at `backend/target/site/jacoco/index.html`.

## 📍IAM Endpoints

✅ All endpoints below have been implemented with standardized responses in `/api/user`:

| route               | description                                          | status
|----------------------|-----------------------------------------------------|--------
| <kbd>POST /api/user/auth/signup</kbd>     | See [request details](#iam-register) | ✅ Done
| <kbd>POST /api/user/auth/login</kbd>     | See [request details](#iam-login) | ✅ Done
| <kbd>GET /api/user/</kbd>     | See [request details](#iam-get-all-users) | ✅ Done
| <kbd>GET /api/user/cpf/{cpf}</kbd>     | See [request details](#iam-get-user-by-cpf) | ✅ Done
| <kbd>PUT /api/user/{id}</kbd>     | See [request details](#iam-update-user) | ✅ Done
| <kbd>DELETE /api/user/{id}</kbd>     | See [request details](#iam-delete-user) | ✅ Done

<h3 id="iam-register">POST /api/user/auth/signup ✅</h3>

**REQUEST**
```json
{
    "email": "user@example.com",
    "password": "Password123!",
    "cpf": "52998224725",
    "role": "CONSUMER",
    "fullName": "John Doe"
}
```

**RESPONSE**
```json
{
    "success": true,
    "message": "User registered successfully",
    "data": {
        "id": "671d1c91f7689b2849534587",
        "email": "user@example.com",
        "cpf": "52998224725",
        "role": "CONSUMER",
        "fullName": "John Doe"
    }
}
```

<h3 id="iam-login">POST /api/user/auth/login ✅</h3>

**REQUEST**
```json
{
    "cpf": "52998224725",
    "password": "Password123!"
}
```

**RESPONSE**
```json
{
    "success": true,
    "message": "Authentication successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "expirationTime": "2023-05-21T12:00:00Z"
    }
}
```

<h3 id="iam-get-all-users">GET /api/user/ ✅</h3>

**RESPONSE**
```json
{
    "success": true,
    "message": "Users retrieved successfully",
    "data": [
        {
            "id": "671d1c91f7689b2849534587",
            "email": "user@example.com",
            "cpf": "52998224725",
            "role": "CONSUMER",
            "fullName": "John Doe"
        },
        {
            "id": "671d1c91f7689b2849534588",
            "email": "admin@example.com",
            "cpf": "12345678901",
            "role": "ADMIN",
            "fullName": "Admin User"
        }
        /* All other users */
    ]
}
```

<h3 id="iam-get-user-by-cpf">GET /api/user/cpf/{cpf} ✅</h3>

**RESPONSE**
```json
{
    "success": true,
    "message": "User retrieved successfully",
    "data": {
        "id": "671d1c91f7689b2849534587",
        "email": "user@example.com",
        "cpf": "52998224725",
        "role": "CONSUMER",
        "fullName": "John Doe"
    }
}
```

<h3 id="iam-update-user">PUT /api/user/{id} ✅</h3>

**REQUEST**
```json
{
    "email": "updated@example.com",
    "password": "NewPassword123!",
    "cpf": "52998224725",
    "role": "CONSUMER",
    "fullName": "John Updated Doe"
}
```

**RESPONSE**
```json
{
    "success": true,
    "message": "User updated successfully",
    "data": {
        "id": "671d1c91f7689b2849534587",
        "email": "updated@example.com",
        "cpf": "52998224725",
        "role": "CONSUMER",
        "fullName": "John Updated Doe"
    }
}
```

<h3 id="iam-delete-user">DELETE /api/user/{id} ✅</h3>

**RESPONSE**
```json
{
    "success": true,
    "message": "User deleted successfully",
    "data": null
}
```
