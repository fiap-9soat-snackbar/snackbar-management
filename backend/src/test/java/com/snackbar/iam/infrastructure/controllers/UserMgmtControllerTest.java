package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.application.ports.in.DeleteUserInputPort;
import com.snackbar.iam.application.ports.in.GetAllUsersInputPort;
import com.snackbar.iam.application.ports.in.GetUserByCpfInputPort;
import com.snackbar.iam.application.ports.in.UpdateUserInputPort;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.DuplicateUserException;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import com.snackbar.iam.infrastructure.controllers.dto.UpdateUserRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Management Controller Tests")
class UserMgmtControllerTest {

    @Mock
    private GetAllUsersInputPort getAllUsersUseCase;
    
    @Mock
    private GetUserByCpfInputPort getUserByCpfUseCase;
    
    @Mock
    private UpdateUserInputPort updateUserUseCase;
    
    @Mock
    private DeleteUserInputPort deleteUserUseCase;
    
    @InjectMocks
    private UserMgmtController controller;
    
    @Nested
    @DisplayName("When getting all users")
    class GetAllUsers {
        
        @Test
        @DisplayName("Should return list of users when users exist")
        void shouldReturnListOfUsersWhenUsersExist() {
            // Given
            User user1 = new User("1", "John Doe", "john@example.com", "52998224725", IamRole.ADMIN, "password");
            User user2 = new User("2", "Jane Smith", "jane@example.com", "40532176871", IamRole.CONSUMER, "password");
            List<User> users = Arrays.asList(user1, user2);
            
            when(getAllUsersUseCase.getAllUsers()).thenReturn(users);
            
            // When
            ResponseEntity<List<UserResponseDTO>> response = controller.getAllUsers();
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<UserResponseDTO> responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals(2, responseBody.size());
            
            UserResponseDTO firstUser = responseBody.get(0);
            assertNotNull(firstUser);
            assertEquals("1", firstUser.id());
            assertEquals("John Doe", firstUser.name());
            assertEquals("john@example.com", firstUser.email());
            assertEquals("52998224725", firstUser.cpf());
            assertEquals(IamRole.ADMIN, firstUser.role());
            
            UserResponseDTO secondUser = responseBody.get(1);
            assertNotNull(secondUser);
            assertEquals("2", secondUser.id());
            assertEquals("Jane Smith", secondUser.name());
            
            verify(getAllUsersUseCase).getAllUsers();
        }
        
        @Test
        @DisplayName("Should return empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsersExist() {
            // Given
            when(getAllUsersUseCase.getAllUsers()).thenReturn(List.of());
            
            // When
            ResponseEntity<List<UserResponseDTO>> response = controller.getAllUsers();
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<UserResponseDTO> responseBody = response.getBody();
            assertNotNull(responseBody);
            assertTrue(responseBody.isEmpty());
            
            verify(getAllUsersUseCase).getAllUsers();
        }
    }
    
    @Nested
    @DisplayName("When getting user by CPF")
    class GetUserByCpf {
        
        @Test
        @DisplayName("Should return user when user exists")
        void shouldReturnUserWhenUserExists() {
            // Given
            String cpf = "52998224725";
            User user = new User("1", "John Doe", "john@example.com", cpf, IamRole.ADMIN, "password");
            
            when(getUserByCpfUseCase.getUserByCpf(cpf)).thenReturn(user);
            
            // When
            ResponseEntity<UserResponseDTO> response = controller.getUserByCpf(cpf);
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            UserResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals("1", responseBody.id());
            assertEquals("John Doe", responseBody.name());
            assertEquals("john@example.com", responseBody.email());
            assertEquals(cpf, responseBody.cpf());
            assertEquals(IamRole.ADMIN, responseBody.role());
            
            verify(getUserByCpfUseCase).getUserByCpf(cpf);
        }
        
        @Test
        @DisplayName("Should return NOT_FOUND when user does not exist")
        void shouldReturnNotFoundWhenUserDoesNotExist() {
            // Given
            String cpf = "52998224725";
            
            when(getUserByCpfUseCase.getUserByCpf(cpf)).thenThrow(new UserNotFoundException("User not found"));
            
            // When
            ResponseEntity<UserResponseDTO> response = controller.getUserByCpf(cpf);
            
            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
            
            verify(getUserByCpfUseCase).getUserByCpf(cpf);
        }
    }
    
    @Nested
    @DisplayName("When updating a user")
    class UpdateUser {
        
        @Test
        @DisplayName("Should return updated user when update is successful")
        void shouldReturnUpdatedUserWhenUpdateIsSuccessful() {
            // Given
            String userId = "1";
            UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
            requestDTO.setName("John Updated");
            requestDTO.setEmail("john.updated@example.com");
            requestDTO.setCpf("52998224725");
            requestDTO.setRole("ADMIN");
            requestDTO.setPassword("newpassword");
            
            User updatedUser = new User(
                    userId,
                    "John Updated",
                    "john.updated@example.com",
                    "52998224725",
                    IamRole.ADMIN,
                    "newpassword"
            );
            
            when(updateUserUseCase.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);
            
            // When
            ResponseEntity<?> response = controller.updateUser(userId, requestDTO);
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            Object responseBody = response.getBody();
            assertNotNull(responseBody);
            assertTrue(responseBody instanceof UserResponseDTO);
            
            UserResponseDTO userDTO = (UserResponseDTO) responseBody;
            assertEquals(userId, userDTO.id());
            assertEquals("John Updated", userDTO.name());
            assertEquals("john.updated@example.com", userDTO.email());
            assertEquals("52998224725", userDTO.cpf());
            assertEquals(IamRole.ADMIN, userDTO.role());
            
            verify(updateUserUseCase).updateUser(eq(userId), any(User.class));
        }
        
        @Test
        @DisplayName("Should return NOT_FOUND when user to update does not exist")
        void shouldReturnNotFoundWhenUserToUpdateDoesNotExist() {
            // Given
            String userId = "999";
            UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
            requestDTO.setName("John Updated");
            requestDTO.setEmail("john.updated@example.com");
            requestDTO.setCpf("52998224725");
            requestDTO.setRole("ADMIN");
            requestDTO.setPassword("newpassword");
            
            doThrow(new UserNotFoundException("User not found"))
                .when(updateUserUseCase).updateUser(eq(userId), any(User.class));
            
            // When
            ResponseEntity<?> response = controller.updateUser(userId, requestDTO);
            
            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
            
            verify(updateUserUseCase).updateUser(eq(userId), any(User.class));
        }
        
        @Test
        @DisplayName("Should return BAD_REQUEST when update causes duplicate user")
        void shouldReturnBadRequestWhenUpdateCausesDuplicateUser() {
            // Given
            String userId = "1";
            UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
            requestDTO.setName("John Updated");
            requestDTO.setEmail("existing@example.com");
            requestDTO.setCpf("52998224725");
            requestDTO.setRole("ADMIN");
            requestDTO.setPassword("newpassword");
            
            String errorMessage = "Email already in use";
            doThrow(new DuplicateUserException(errorMessage))
                .when(updateUserUseCase).updateUser(eq(userId), any(User.class));
            
            // When
            ResponseEntity<?> response = controller.updateUser(userId, requestDTO);
            
            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            Object responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals(errorMessage, responseBody);
            
            verify(updateUserUseCase).updateUser(eq(userId), any(User.class));
        }
    }
    
    @Nested
    @DisplayName("When deleting a user")
    class DeleteUser {
        
        @Test
        @DisplayName("Should return NO_CONTENT when deletion is successful")
        void shouldReturnNoContentWhenDeletionIsSuccessful() {
            // Given
            String userId = "1";
            doNothing().when(deleteUserUseCase).deleteUser(userId);
            
            // When
            ResponseEntity<Void> response = controller.deleteUser(userId);
            
            // Then
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            
            verify(deleteUserUseCase).deleteUser(userId);
        }
        
        @Test
        @DisplayName("Should return NOT_FOUND when user to delete does not exist")
        void shouldReturnNotFoundWhenUserToDeleteDoesNotExist() {
            // Given
            String userId = "999";
            doThrow(new UserNotFoundException("User not found")).when(deleteUserUseCase).deleteUser(userId);
            
            // When
            ResponseEntity<Void> response = controller.deleteUser(userId);
            
            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
            
            verify(deleteUserUseCase).deleteUser(userId);
        }
    }
}
