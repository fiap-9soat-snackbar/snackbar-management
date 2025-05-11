package com.snackbar.iam.infrastructure.persistence;

import com.snackbar.iam.domain.IamRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {
        @Test
        @DisplayName("Should return true when comparing same object instance")
        void shouldReturnTrueWhenComparingSameObjectInstance() {
            // Given
            UserEntity userEntity = new UserEntity("1", "Test User", "test@example.com", "12345678900", IamRole.CONSUMER, "password");
            
            // When & Then
            assertTrue(userEntity.equals(userEntity), "An object should be equal to itself");
        }
        
        @Test
        @DisplayName("Should return false when comparing with null")
        void shouldReturnFalseWhenComparingWithNull() {
            // Given
            UserEntity userEntity = new UserEntity("1", "Test User", "test@example.com", "12345678900", IamRole.CONSUMER, "password");
            
            // When & Then
            assertFalse(userEntity.equals(null), "An object should not be equal to null");
        }
        
        @Test
        @DisplayName("Should return false when comparing with different class")
        void shouldReturnFalseWhenComparingWithDifferentClass() {
            // Given
            UserEntity userEntity = new UserEntity("1", "Test User", "test@example.com", "12345678900", IamRole.CONSUMER, "password");
            
            // When & Then
            assertFalse(userEntity.equals("Not a UserEntity"), "An object should not be equal to an object of a different class");
        }
        
        @Test
        @DisplayName("Should return true when id, cpf, and email are equal")
        void shouldReturnTrueWhenIdCpfAndEmailAreEqual() {
            // Given
            UserEntity userEntity1 = new UserEntity("1", "Test User 1", "test@example.com", "12345678900", IamRole.CONSUMER, "password1");
            UserEntity userEntity2 = new UserEntity("1", "Test User 2", "test@example.com", "12345678900", IamRole.ADMIN, "password2");
            
            // When & Then
            assertTrue(userEntity1.equals(userEntity2), "Entities with same id, email, and cpf should be equal");
            assertEquals(userEntity1.hashCode(), userEntity2.hashCode(), "Hash codes should be equal for equal entities");
        }
        
        @Test
        @DisplayName("Should return false when id is different")
        void shouldReturnFalseWhenIdIsDifferent() {
            // Given
            UserEntity userEntity1 = new UserEntity("1", "Test User", "test@example.com", "12345678900", IamRole.CONSUMER, "password");
            UserEntity userEntity2 = new UserEntity("2", "Test User", "test@example.com", "12345678900", IamRole.CONSUMER, "password");
            
            // When & Then
            assertFalse(userEntity1.equals(userEntity2), "Entities with different ids should not be equal");
            assertNotEquals(userEntity1.hashCode(), userEntity2.hashCode(), "Hash codes should differ for entities with different ids");
        }
        
        @Test
        @DisplayName("Should return false when email is different")
        void shouldReturnFalseWhenEmailIsDifferent() {
            // Given
            UserEntity userEntity1 = new UserEntity("1", "Test User", "test1@example.com", "12345678900", IamRole.CONSUMER, "password");
            UserEntity userEntity2 = new UserEntity("1", "Test User", "test2@example.com", "12345678900", IamRole.CONSUMER, "password");
            
            // When & Then
            assertFalse(userEntity1.equals(userEntity2), "Entities with different emails should not be equal");
            assertNotEquals(userEntity1.hashCode(), userEntity2.hashCode(), "Hash codes should differ for entities with different emails");
        }
        
        @Test
        @DisplayName("Should return false when cpf is different")
        void shouldReturnFalseWhenCpfIsDifferent() {
            // Given
            UserEntity userEntity1 = new UserEntity("1", "Test User", "test@example.com", "12345678900", IamRole.CONSUMER, "password");
            UserEntity userEntity2 = new UserEntity("1", "Test User", "test@example.com", "98765432100", IamRole.CONSUMER, "password");
            
            // When & Then
            assertFalse(userEntity1.equals(userEntity2), "Entities with different CPFs should not be equal");
            assertNotEquals(userEntity1.hashCode(), userEntity2.hashCode(), "Hash codes should differ for entities with different CPFs");
        }
        
        @Test
        @DisplayName("Should handle null fields in equals comparison")
        void shouldHandleNullFieldsInEqualsComparison() {
            // Given
            UserEntity userEntity1 = new UserEntity(null, "Test User", null, null, IamRole.CONSUMER, "password");
            UserEntity userEntity2 = new UserEntity(null, "Test User", null, null, IamRole.CONSUMER, "password");
            UserEntity userEntity3 = new UserEntity("1", "Test User", null, null, IamRole.CONSUMER, "password");
            
            // When & Then
            assertTrue(userEntity1.equals(userEntity2), "Entities with same null fields should be equal");
            assertEquals(userEntity1.hashCode(), userEntity2.hashCode(), "Hash codes should be equal for entities with same null fields");
            assertFalse(userEntity1.equals(userEntity3), "Entity with null id should not equal entity with non-null id");
        }
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
