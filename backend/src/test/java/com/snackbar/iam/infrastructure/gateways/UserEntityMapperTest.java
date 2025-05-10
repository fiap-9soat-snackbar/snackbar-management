package com.snackbar.iam.infrastructure.gateways;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityMapperTest {

    @Test
    @DisplayName("Should map domain User to UserEntity")
    void shouldMapDomainUserToUserEntity() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Test User";
        String email = "test@example.com";
        String cpf = "52998224725";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";
        
        User user = new User(id, name, email, cpf, role, password);
        
        // When
        UserEntity entity = UserEntityMapper.toEntity(user);
        
        // Then
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
        assertEquals(email, entity.getEmail());
        assertEquals(cpf, entity.getCpf());
        assertEquals(role, entity.getRole());
        assertEquals(password, entity.getPassword());
    }
    
    @Test
    @DisplayName("Should map UserEntity to domain User")
    void shouldMapUserEntityToDomainUser() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Test User";
        String email = "test@example.com";
        String cpf = "52998224725";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";
        
        UserEntity entity = UserEntity.builder()
                .id(id)
                .name(name)
                .email(email)
                .cpf(cpf)
                .role(role)
                .password(password)
                .build();
        
        // When
        User user = UserEntityMapper.toDomain(entity);
        
        // Then
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(cpf, user.getCpf());
        assertEquals(role, user.getRole());
        assertEquals(password, user.getPassword());
    }
    
    @Test
    @DisplayName("Should handle null when mapping domain User to UserEntity")
    void shouldHandleNullWhenMappingDomainUserToUserEntity() {
        // Given
        User user = null;
        
        // When
        UserEntity entity = UserEntityMapper.toEntity(user);
        
        // Then
        assertNull(entity);
    }
    
    @Test
    @DisplayName("Should handle null when mapping UserEntity to domain User")
    void shouldHandleNullWhenMappingUserEntityToDomainUser() {
        // Given
        UserEntity entity = null;
        
        // When
        User user = UserEntityMapper.toDomain(entity);
        
        // Then
        assertNull(user);
    }
}
