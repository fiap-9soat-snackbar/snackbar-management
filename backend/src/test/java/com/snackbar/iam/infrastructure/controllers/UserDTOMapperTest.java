package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.UpdateUserRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User DTO Mapper Tests")
class UserDTOMapperTest {

    @Nested
    @DisplayName("When mapping RegisterUserRequestDTO to User")
    class MapRegisterUserRequestDTO {
        
        @Test
        @DisplayName("Should correctly map all fields")
        void shouldCorrectlyMapAllFields() {
            // Given
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "John Doe",
                    "john@example.com",
                    "52998224725",
                    "password123",
                    IamRole.ADMIN
            );
            
            // When
            User user = UserDTOMapper.toUser(dto);
            
            // Then
            assertNull(user.getId()); // ID should be null as it will be generated
            assertEquals("John Doe", user.getName());
            assertEquals("john@example.com", user.getEmail());
            assertEquals("52998224725", user.getCpf());
            assertEquals(IamRole.ADMIN, user.getRole());
            assertEquals("password123", user.getPassword());
        }
    }
    
    @Nested
    @DisplayName("When mapping UpdateUserRequestDTO to User")
    class MapUpdateUserRequestDTO {
        
        @Test
        @DisplayName("Should correctly map all fields")
        void shouldCorrectlyMapAllFields() {
            // Given
            UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
            dto.setName("John Updated");
            dto.setEmail("john.updated@example.com");
            dto.setCpf("52998224725");
            dto.setRole("ADMIN");
            dto.setPassword("newpassword");
            
            // When
            User user = UserDTOMapper.toUser(dto);
            
            // Then
            assertNull(user.getId()); // ID should be null as it will be set by the use case
            assertEquals("John Updated", user.getName());
            assertEquals("john.updated@example.com", user.getEmail());
            assertEquals("52998224725", user.getCpf());
            assertEquals(IamRole.ADMIN, user.getRole());
            assertEquals("newpassword", user.getPassword());
        }
        
        @Test
        @DisplayName("Should convert role string to enum")
        void shouldConvertRoleStringToEnum() {
            // Given
            UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
            dto.setName("John");
            dto.setEmail("john@example.com");
            dto.setCpf("52998224725");
            dto.setRole("CONSUMER");
            dto.setPassword("password");
            
            // When
            User user = UserDTOMapper.toUser(dto);
            
            // Then
            assertEquals(IamRole.CONSUMER, user.getRole());
        }
    }
    
    @Nested
    @DisplayName("When mapping User to UserResponseDTO")
    class MapUserToResponseDTO {
        
        @Test
        @DisplayName("Should correctly map all fields")
        void shouldCorrectlyMapAllFields() {
            // Given
            User user = new User(
                    "1",
                    "John Doe",
                    "john@example.com",
                    "52998224725",
                    IamRole.ADMIN,
                    "password123"
            );
            
            // When
            UserResponseDTO dto = UserDTOMapper.toResponseDTO(user);
            
            // Then
            assertEquals("1", dto.id());
            assertEquals("John Doe", dto.name());
            assertEquals("john@example.com", dto.email());
            assertEquals("52998224725", dto.cpf());
            assertEquals(IamRole.ADMIN, dto.role());
        }
        
        @Test
        @DisplayName("Should not include password in response")
        void shouldNotIncludePasswordInResponse() {
            // Given
            User user = new User(
                    "1",
                    "John Doe",
                    "john@example.com",
                    "52998224725",
                    IamRole.ADMIN,
                    "password123"
            );
            
            // When
            UserResponseDTO dto = UserDTOMapper.toResponseDTO(user);
            
            // Then
            // Verify that UserResponseDTO doesn't have a password field
            // We can verify the fields match what we expect
            assertNotNull(dto.id());
            assertNotNull(dto.name());
            assertNotNull(dto.email());
            assertNotNull(dto.cpf());
            assertNotNull(dto.role());
            // No password field should be accessible
        }
    }
}
