package com.snackbar.iam.infrastructure.persistence;

import com.snackbar.iam.domain.IamRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    @DisplayName("Should create a user entity with all fields")
    void shouldCreateUserEntityWithAllFields() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Test User";
        String email = "test@example.com";
        String cpf = "52998224725";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When
        UserEntity userEntity = new UserEntity(id, name, email, cpf, role, password);

        // Then
        assertEquals(id, userEntity.getId());
        assertEquals(name, userEntity.getName());
        assertEquals(email, userEntity.getEmail());
        assertEquals(cpf, userEntity.getCpf());
        assertEquals(role, userEntity.getRole());
        assertEquals(password, userEntity.getPassword());
    }

    @Test
    @DisplayName("Should create a user entity using builder")
    void shouldCreateUserEntityUsingBuilder() {
        // Given
        String id = UUID.randomUUID().toString();
        String name = "Test User";
        String email = "test@example.com";
        String cpf = "52998224725";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When
        UserEntity userEntity = UserEntity.builder()
                .id(id)
                .name(name)
                .email(email)
                .cpf(cpf)
                .role(role)
                .password(password)
                .build();

        // Then
        assertEquals(id, userEntity.getId());
        assertEquals(name, userEntity.getName());
        assertEquals(email, userEntity.getEmail());
        assertEquals(cpf, userEntity.getCpf());
        assertEquals(role, userEntity.getRole());
        assertEquals(password, userEntity.getPassword());
    }

    @Test
    @DisplayName("Should use setters to update user entity fields")
    void shouldUseSettersToUpdateUserEntityFields() {
        // Given
        UserEntity userEntity = new UserEntity();
        String id = UUID.randomUUID().toString();
        String name = "Test User";
        String email = "test@example.com";
        String cpf = "52998224725";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When
        userEntity.setId(id);
        userEntity.setName(name);
        userEntity.setEmail(email);
        userEntity.setCpf(cpf);
        userEntity.setRole(role);
        userEntity.setPassword(password);

        // Then
        assertEquals(id, userEntity.getId());
        assertEquals(name, userEntity.getName());
        assertEquals(email, userEntity.getEmail());
        assertEquals(cpf, userEntity.getCpf());
        assertEquals(role, userEntity.getRole());
        assertEquals(password, userEntity.getPassword());
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void shouldCorrectlyImplementEqualsAndHashCode() {
        // Given
        String id = UUID.randomUUID().toString();
        String name1 = "Test User 1";
        String name2 = "Test User 2";
        String email = "test@example.com";
        String cpf = "52998224725";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When
        UserEntity userEntity1 = new UserEntity(id, name1, email, cpf, role, password);
        UserEntity userEntity2 = new UserEntity(id, name2, email, cpf, role, password);
        UserEntity userEntity3 = new UserEntity(UUID.randomUUID().toString(), name1, email, cpf, role, password);

        // Then
        assertEquals(userEntity1, userEntity2, "Entities with same id, email, and cpf should be equal");
        assertNotEquals(userEntity1, userEntity3, "Entities with different ids should not be equal");
        assertEquals(userEntity1.hashCode(), userEntity2.hashCode(), "Hash codes should be equal for equal entities");
        assertNotEquals(userEntity1.hashCode(), userEntity3.hashCode(), "Hash codes should differ for different entities");
    }

    @Test
    @DisplayName("Should correctly implement toString")
    void shouldCorrectlyImplementToString() {
        // Given
        String id = "test-id";
        String name = "Test User";
        String email = "test@example.com";
        String cpf = "52998224725";
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When
        UserEntity userEntity = new UserEntity(id, name, email, cpf, role, password);
        String toString = userEntity.toString();

        // Then
        assertTrue(toString.contains(id));
        assertTrue(toString.contains(name));
        assertTrue(toString.contains(email));
        assertTrue(toString.contains(cpf));
        assertTrue(toString.contains(role.toString()));
        assertFalse(toString.contains(password), "Password should not be included in toString");
    }
}
