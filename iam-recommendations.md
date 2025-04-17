# IAM Module Recommendations

## Issues and Solutions:

### 1. Repository Type Mismatch

**Issue:** The `IamRepository` is defined to work with `UserEntity` but returns `UserDetailsEntity` in its methods.

```java
public interface IamRepository extends MongoRepository<UserEntity, String> {
    Optional<UserDetailsEntity> findByEmail(String email);
    Optional<UserDetailsEntity> findByCpf(String cpf);
}
```

**Solution:** Modify the repository to be consistent with the entity type:

```java
public interface IamRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByCpf(String cpf);
}
```

### 2. Entity Duplication

**Issue:** There are two nearly identical entities: `UserEntity` and `UserDetailsEntity`, causing confusion and potential data inconsistency.

**Solution:** Consolidate into a single entity class that implements `UserDetails`:

```java
@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {
    @Id
    private String id;
    
    private String name;
    private String email;
    private String cpf;
    private IamRole role;
    private String password;

    // Implement UserDetails methods here
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return cpf;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Other UserDetails methods...
}
```

### 3. Authentication Service Type Inconsistency

**Issue:** `AuthenticationService.authenticate()` returns `UserDetailsEntity` but `AuthenticationService.signup()` returns `UserEntity`.

**Solution:** Make both methods return the same type:

```java
public UserEntity authenticate(LoginUserDto input) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    input.getCpf(),
                    input.getPassword()
            )
    );

    return findByCpf(input.getCpf());
}

public UserEntity findByCpf(String cpf) {
    return userRepository.findByCpf(cpf)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para o CPF: " + cpf));
}
```

### 4. Anonymous Login Issue

**Issue:** In `AuthenticationController.authenticate()`, when handling anonymous login, it creates a new empty `UserDetailsEntity` without proper initialization.

**Solution:** Create a proper anonymous user with minimal required fields:

```java
if (Boolean.TRUE.equals(loginUserDto.getAnonymous())) {
    authenticatedUser = UserDetailsEntity.builder()
        .cpf("anonymous")
        .role(IamRole.CONSUMER)
        .build();
    jwtToken = jwtService.generateToken(authenticatedUser);
} else {
    // existing code...
}
```

### 5. Redundant Repository

**Issue:** Both `IamRepository` and `UserRepository` serve the same purpose with identical methods.

**Solution:** Consolidate into a single repository:

```java
public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByCpf(String cpf);
}
```

And update all service classes to use this single repository.

### 6. Missing Authority Implementation

**Issue:** The `getAuthorities()` method in `UserDetailsEntity` returns an empty list, which could cause authorization issues.

**Solution:** Implement proper authorities based on user roles:

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
}
```

### 7. Redundant Annotations

**Issue:** In `UserEntity` and other classes, there are redundant annotations like `@Data`, `@Getter`, and `@Setter`.

**Solution:** Use only `@Data` which includes both getter and setter functionality:

```java
@Document(collection = "user")
@Data
@Builder
public class UserEntity {
    // fields...
}
```

### 8. Potential NPE in Anonymous Login

**Issue:** When handling anonymous login, the code might throw NullPointerException if `loginUserDto.getAnonymous()` is null.

**Solution:** Use a safer null check:

```java
if (Boolean.TRUE.equals(loginUserDto.getAnonymous())) {
    // anonymous login logic
}
```
