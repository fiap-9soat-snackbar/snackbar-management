package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.application.ports.in.AuthenticateUserInputPort;
import com.snackbar.iam.application.ports.in.RegisterUserInputPort;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.InvalidCredentialsException;
import com.snackbar.iam.infrastructure.controllers.dto.AuthenticationResponseDTO;
import com.snackbar.iam.infrastructure.controllers.dto.LoginRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;
import com.snackbar.iam.infrastructure.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Authentication Controller Tests")
class UserAuthControllerTest {

    @Mock
    private RegisterUserInputPort registerUserUseCase;
    
    @Mock
    private AuthenticateUserInputPort authenticateUserUseCase;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private UserAuthController controller;
    
    @Nested
    @DisplayName("When registering a user")
    class RegisterUser {
        
        @Test
        @DisplayName("Should return CREATED status with user data when registration is successful")
        void shouldReturnCreatedStatusWithUserData() {
            // Given
            RegisterUserRequestDTO requestDTO = new RegisterUserRequestDTO(
                    "John Doe",
                    "john@example.com",
                    "52998224725",
                    "password123",
                    IamRole.CONSUMER
            );
            
            User registeredUser = new User(
                    "1",
                    "John Doe",
                    "john@example.com",
                    "52998224725",
                    IamRole.CONSUMER,
                    "password123"
            );
            
            when(registerUserUseCase.registerUser(any(User.class))).thenReturn(registeredUser);
            
            // When
            ResponseEntity<UserResponseDTO> response = controller.registerUser(requestDTO);
            
            // Then
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            UserResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals("1", responseBody.id());
            assertEquals("John Doe", responseBody.name());
            assertEquals("john@example.com", responseBody.email());
            assertEquals("52998224725", responseBody.cpf());
            assertEquals(IamRole.CONSUMER, responseBody.role());
            
            verify(registerUserUseCase).registerUser(any(User.class));
        }
    }
    
    @Nested
    @DisplayName("When logging in a user")
    class LoginUser {
        
        @Test
        @DisplayName("Should return OK status with token when login is successful")
        void shouldReturnOkStatusWithToken() {
            // Given
            LoginRequestDTO requestDTO = new LoginRequestDTO("52998224725", "password123", false);
            
            User authenticatedUser = new User(
                    "1",
                    "John Doe",
                    "john@example.com",
                    "52998224725",
                    IamRole.CONSUMER,
                    "password123"
            );
            
            when(authenticateUserUseCase.authenticate("52998224725", "password123")).thenReturn(authenticatedUser);
            when(jwtService.generateToken(authenticatedUser)).thenReturn("jwt-token");
            when(jwtService.getExpirationTime()).thenReturn(3600L);
            
            // When
            ResponseEntity<AuthenticationResponseDTO> response = controller.loginUser(requestDTO);
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            AuthenticationResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals("jwt-token", responseBody.token());
            assertEquals(3600L, responseBody.expiresIn());
            
            UserResponseDTO userDTO = responseBody.user();
            assertNotNull(userDTO);
            assertEquals("1", userDTO.id());
            assertEquals("John Doe", userDTO.name());
            
            verify(authenticateUserUseCase).authenticate("52998224725", "password123");
            verify(jwtService).generateToken(authenticatedUser);
            verify(jwtService).getExpirationTime();
        }
        
        @Test
        @DisplayName("Should return UNAUTHORIZED status when credentials are invalid")
        void shouldReturnUnauthorizedStatusWhenCredentialsAreInvalid() {
            // Given
            LoginRequestDTO requestDTO = new LoginRequestDTO("52998224725", "wrong-password", false);
            
            when(authenticateUserUseCase.authenticate("52998224725", "wrong-password"))
                    .thenThrow(new InvalidCredentialsException("Invalid credentials"));
            
            // When
            ResponseEntity<AuthenticationResponseDTO> response = controller.loginUser(requestDTO);
            
            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNull(response.getBody());
            
            verify(authenticateUserUseCase).authenticate("52998224725", "wrong-password");
            verifyNoInteractions(jwtService);
        }
        
        @Test
        @DisplayName("Should handle anonymous login when requested")
        void shouldHandleAnonymousLoginWhenRequested() {
            // Given
            LoginRequestDTO requestDTO = new LoginRequestDTO(null, null, true);
            
            // Use mock instead of creating a real User object to avoid validation
            User anonymousUser = mock(User.class);
            when(anonymousUser.getId()).thenReturn("anonymous");
            when(anonymousUser.getName()).thenReturn("Anonymous User");
            when(anonymousUser.getEmail()).thenReturn("anonymous@example.com");
            when(anonymousUser.getRole()).thenReturn(IamRole.CONSUMER);
            
            when(authenticateUserUseCase.authenticateAnonymous()).thenReturn(anonymousUser);
            when(jwtService.generateToken(anonymousUser)).thenReturn("anonymous-jwt-token");
            when(jwtService.getExpirationTime()).thenReturn(1800L);
            
            // When
            ResponseEntity<AuthenticationResponseDTO> response = controller.loginUser(requestDTO);
            
            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            AuthenticationResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertEquals("anonymous-jwt-token", responseBody.token());
            assertEquals(1800L, responseBody.expiresIn());
            
            UserResponseDTO userDTO = responseBody.user();
            assertNotNull(userDTO);
            assertEquals("anonymous", userDTO.id());
            assertEquals("Anonymous User", userDTO.name());
            
            verify(authenticateUserUseCase).authenticateAnonymous();
            verify(jwtService).generateToken(anonymousUser);
            verify(jwtService).getExpirationTime();
        }
    }
}
