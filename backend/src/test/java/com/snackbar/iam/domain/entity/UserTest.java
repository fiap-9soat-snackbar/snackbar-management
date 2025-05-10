package com.snackbar.iam.domain.entity;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.exceptions.InvalidUserDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateValidUser() {
        // Given: Valid user data
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String cpf = "529.982.247-25"; // Valid CPF
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When: Creating a user with valid data
        User user = new User(id, name, email, cpf, role, password);

        // Then: User should be created with the provided data
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(cpf, user.getCpf());
        assertEquals(role, user.getRole());
        assertEquals(password, user.getPassword());
    }

    @Test
    void shouldCreateUserWithFormattedCpf() {
        // Given: Valid user data with formatted CPF
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String cpf = "529.982.247-25"; // Valid CPF with formatting
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When: Creating a user with formatted CPF
        User user = new User(id, name, email, cpf, role, password);

        // Then: User should be created successfully
        assertEquals(cpf, user.getCpf());
    }

    @Test
    void shouldCreateUserWithUnformattedCpf() {
        // Given: Valid user data with unformatted CPF
        String id = "123";
        String name = "John Doe";
        String email = "john.doe@example.com";
        String cpf = "52998224725"; // Valid CPF without formatting
        IamRole role = IamRole.CONSUMER;
        String password = "password123";

        // When: Creating a user with unformatted CPF
        User user = new User(id, name, email, cpf, role, password);

        // Then: User should be created successfully
        assertEquals(cpf, user.getCpf());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserData")
    void shouldThrowExceptionForInvalidUserData(String name, String email, String cpf, IamRole role, String password, String expectedErrorMessage) {
        // Given: Invalid user data provided by the method source

        // When/Then: Creating a user with invalid data should throw an exception
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> new User("123", name, email, cpf, role, password)
        );
        
        // Then: The exception message should contain the expected error
        assertTrue(exception.getMessage().contains(expectedErrorMessage));
    }

    private static Stream<Arguments> provideInvalidUserData() {
        return Stream.of(
            // Invalid name
            Arguments.of(null, "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, "password123", "Name cannot be empty"),
            Arguments.of("", "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, "password123", "Name cannot be empty"),
            Arguments.of("   ", "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, "password123", "Name cannot be empty"),
            
            // Invalid email
            Arguments.of("John Doe", null, "529.982.247-25", IamRole.CONSUMER, "password123", "Email cannot be empty"),
            Arguments.of("John Doe", "", "529.982.247-25", IamRole.CONSUMER, "password123", "Email cannot be empty"),
            Arguments.of("John Doe", "   ", "529.982.247-25", IamRole.CONSUMER, "password123", "Email cannot be empty"),
            Arguments.of("John Doe", "invalid-email", "529.982.247-25", IamRole.CONSUMER, "password123", "Invalid email format"),
            
            // Invalid CPF
            Arguments.of("John Doe", "john.doe@example.com", null, IamRole.CONSUMER, "password123", "CPF cannot be empty"),
            Arguments.of("John Doe", "john.doe@example.com", "", IamRole.CONSUMER, "password123", "CPF cannot be empty"),
            Arguments.of("John Doe", "john.doe@example.com", "   ", IamRole.CONSUMER, "password123", "CPF cannot be empty"),
            Arguments.of("John Doe", "john.doe@example.com", "123456789", IamRole.CONSUMER, "password123", "Invalid CPF format"),
            Arguments.of("John Doe", "john.doe@example.com", "11111111111", IamRole.CONSUMER, "password123", "Invalid CPF format"),
            
            // Invalid role
            Arguments.of("John Doe", "john.doe@example.com", "529.982.247-25", null, "password123", "Role cannot be null"),
            
            // Invalid password
            Arguments.of("John Doe", "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, null, "Password cannot be empty"),
            Arguments.of("John Doe", "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, "", "Password cannot be empty"),
            Arguments.of("John Doe", "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, "   ", "Password cannot be empty"),
            Arguments.of("John Doe", "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, "short", "Password must be at least 8 characters long")
        );
    }

    @Test
    void shouldAllowSettingUserProperties() {
        // Given: A valid user
        User user = new User("123", "John Doe", "john.doe@example.com", "529.982.247-25", IamRole.CONSUMER, "password123");
        
        // When: Setting new values for user properties
        String newId = "456";
        String newName = "Jane Doe";
        String newEmail = "jane.doe@example.com";
        String newCpf = "853.992.320-81";
        IamRole newRole = IamRole.ADMIN;
        String newPassword = "newpassword123";
        
        user.setId(newId);
        user.setName(newName);
        user.setEmail(newEmail);
        user.setCpf(newCpf);
        user.setRole(newRole);
        user.setPassword(newPassword);
        
        // Then: User properties should be updated
        assertEquals(newId, user.getId());
        assertEquals(newName, user.getName());
        assertEquals(newEmail, user.getEmail());
        assertEquals(newCpf, user.getCpf());
        assertEquals(newRole, user.getRole());
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void shouldCreateUserWithDefaultConstructor() {
        // Given: The need to create a user with the default constructor
        
        // When: Creating a user with the default constructor
        User user = new User();
        
        // Then: User should be created with null properties
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getCpf());
        assertNull(user.getRole());
        assertNull(user.getPassword());
    }
}
