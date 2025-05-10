package com.snackbar.iam.infrastructure.gateways;

import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import com.snackbar.iam.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryGatewayTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @Mock
    private UserEntity mockUserEntity;

    private UserRepositoryGateway userRepositoryGateway;

    @BeforeEach
    void setUp() {
        userRepositoryGateway = new UserRepositoryGateway(userRepository);
    }

    @Test
    @DisplayName("Should create a user")
    void shouldCreateUser() {
        // Given
        try (MockedStatic<UserEntityMapper> mockedMapper = Mockito.mockStatic(UserEntityMapper.class)) {
            mockedMapper.when(() -> UserEntityMapper.toEntity(mockUser)).thenReturn(mockUserEntity);
            mockedMapper.when(() -> UserEntityMapper.toDomain(mockUserEntity)).thenReturn(mockUser);
            
            when(userRepository.save(mockUserEntity)).thenReturn(mockUserEntity);
            
            // When
            User createdUser = userRepositoryGateway.createUser(mockUser);
            
            // Then
            assertNotNull(createdUser);
            assertEquals(mockUser, createdUser);
            
            verify(userRepository).save(mockUserEntity);
        }
    }

    @Test
    @DisplayName("Should update a user")
    void shouldUpdateUser() {
        // Given
        String id = UUID.randomUUID().toString();
        when(mockUser.getId()).thenReturn(id);
        
        try (MockedStatic<UserEntityMapper> mockedMapper = Mockito.mockStatic(UserEntityMapper.class)) {
            mockedMapper.when(() -> UserEntityMapper.toEntity(mockUser)).thenReturn(mockUserEntity);
            mockedMapper.when(() -> UserEntityMapper.toDomain(mockUserEntity)).thenReturn(mockUser);
            
            when(userRepository.findById(id)).thenReturn(Optional.of(mockUserEntity));
            when(userRepository.save(mockUserEntity)).thenReturn(mockUserEntity);
            
            // When
            User updatedUser = userRepositoryGateway.updateUser(mockUser);
            
            // Then
            assertNotNull(updatedUser);
            assertEquals(mockUser, updatedUser);
            
            verify(userRepository).findById(id);
            verify(userRepository).save(mockUserEntity);
        }
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        String id = UUID.randomUUID().toString();
        when(mockUser.getId()).thenReturn(id);
        
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> userRepositoryGateway.updateUser(mockUser));
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should find user by CPF")
    void shouldFindUserByCpf() {
        // Given
        String cpf = "52998224725"; // Valid Brazilian CPF without dots and dashes
        
        try (MockedStatic<UserEntityMapper> mockedMapper = Mockito.mockStatic(UserEntityMapper.class)) {
            mockedMapper.when(() -> UserEntityMapper.toDomain(mockUserEntity)).thenReturn(mockUser);
            
            when(userRepository.findByCpf(cpf)).thenReturn(Optional.of(mockUserEntity));
            
            // When
            Optional<User> foundUser = userRepositoryGateway.findByCpf(cpf);
            
            // Then
            assertTrue(foundUser.isPresent());
            assertEquals(mockUser, foundUser.get());
            
            verify(userRepository).findByCpf(cpf);
        }
    }

    @Test
    @DisplayName("Should return empty when user not found by CPF")
    void shouldReturnEmptyWhenUserNotFoundByCpf() {
        // Given
        String cpf = "52998224725"; // Valid Brazilian CPF without dots and dashes
        
        when(userRepository.findByCpf(cpf)).thenReturn(Optional.empty());
        
        // When
        Optional<User> foundUser = userRepositoryGateway.findByCpf(cpf);
        
        // Then
        assertFalse(foundUser.isPresent());
        verify(userRepository).findByCpf(cpf);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        String email = "test@example.com";
        
        try (MockedStatic<UserEntityMapper> mockedMapper = Mockito.mockStatic(UserEntityMapper.class)) {
            mockedMapper.when(() -> UserEntityMapper.toDomain(mockUserEntity)).thenReturn(mockUser);
            
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUserEntity));
            
            // When
            Optional<User> foundUser = userRepositoryGateway.findByEmail(email);
            
            // Then
            assertTrue(foundUser.isPresent());
            assertEquals(mockUser, foundUser.get());
            
            verify(userRepository).findByEmail(email);
        }
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        String id = UUID.randomUUID().toString();
        
        try (MockedStatic<UserEntityMapper> mockedMapper = Mockito.mockStatic(UserEntityMapper.class)) {
            mockedMapper.when(() -> UserEntityMapper.toDomain(mockUserEntity)).thenReturn(mockUser);
            
            when(userRepository.findById(id)).thenReturn(Optional.of(mockUserEntity));
            
            // When
            Optional<User> foundUser = userRepositoryGateway.findById(id);
            
            // Then
            assertTrue(foundUser.isPresent());
            assertEquals(mockUser, foundUser.get());
            
            verify(userRepository).findById(id);
        }
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        // Given
        UserEntity mockUserEntity1 = mock(UserEntity.class);
        UserEntity mockUserEntity2 = mock(UserEntity.class);
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        
        try (MockedStatic<UserEntityMapper> mockedMapper = Mockito.mockStatic(UserEntityMapper.class)) {
            mockedMapper.when(() -> UserEntityMapper.toDomain(mockUserEntity1)).thenReturn(mockUser1);
            mockedMapper.when(() -> UserEntityMapper.toDomain(mockUserEntity2)).thenReturn(mockUser2);
            
            when(userRepository.findAll()).thenReturn(Arrays.asList(mockUserEntity1, mockUserEntity2));
            
            // When
            List<User> users = userRepositoryGateway.findAll();
            
            // Then
            assertEquals(2, users.size());
            assertEquals(mockUser1, users.get(0));
            assertEquals(mockUser2, users.get(1));
            
            verify(userRepository).findAll();
        }
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUserById() {
        // Given
        String id = UUID.randomUUID().toString();
        
        // When
        userRepositoryGateway.deleteById(id);
        
        // Then
        verify(userRepository).deleteById(id);
    }
}
