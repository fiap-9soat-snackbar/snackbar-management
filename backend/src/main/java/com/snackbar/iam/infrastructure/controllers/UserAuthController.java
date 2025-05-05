package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.application.ports.in.AuthenticateUserInputPort;
import com.snackbar.iam.application.ports.in.RegisterUserInputPort;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.InvalidCredentialsException;
import com.snackbar.iam.infrastructure.controllers.dto.AuthenticationResponseDTO;
import com.snackbar.iam.infrastructure.controllers.dto.LoginRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;
import com.snackbar.iam.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller for user authentication operations.
 * Handles user registration and login.
 */
@RestController
@RequestMapping("/api/user/auth")
public class UserAuthController {

    private final RegisterUserInputPort registerUserUseCase;
    private final AuthenticateUserInputPort authenticateUserUseCase;
    private final JwtService jwtService;

    public UserAuthController(
            RegisterUserInputPort registerUserUseCase,
            AuthenticateUserInputPort authenticateUserUseCase,
            @Qualifier("jwtService") JwtService jwtService) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint for user registration.
     *
     * @param requestDTO The registration request data
     * @return ResponseEntity with the created user information
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterUserRequestDTO requestDTO) {
        // Convert DTO to domain entity
        User userToRegister = UserDTOMapper.toUser(requestDTO);
        
        // Register the user using the use case
        User registeredUser = registerUserUseCase.registerUser(userToRegister);
        
        // Convert domain entity to response DTO
        UserResponseDTO responseDTO = UserDTOMapper.toResponseDTO(registeredUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Endpoint for user login.
     *
     * @param requestDTO The login request data
     * @return ResponseEntity with authentication token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO requestDTO) {
        try {
            // Handle anonymous login if requested
            if (Boolean.TRUE.equals(requestDTO.anonymous())) {
                return handleAnonymousLogin();
            }
            
            // Authenticate the user using the use case
            User authenticatedUser = authenticateUserUseCase.authenticate(
                    requestDTO.cpf(), 
                    requestDTO.password()
            );
            
            // Generate JWT token
            String token = jwtService.generateToken(authenticatedUser);
            long expiresIn = jwtService.getExpirationTime();
            
            // Create response with user details
            UserResponseDTO userDTO = UserDTOMapper.toResponseDTO(authenticatedUser);
            AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(token, expiresIn, userDTO);
            
            return ResponseEntity.ok(responseDTO);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Handles anonymous login requests.
     *
     * @return ResponseEntity with authentication token for anonymous user
     */
    private ResponseEntity<AuthenticationResponseDTO> handleAnonymousLogin() {
        // Use the authenticate method with anonymous flag
        User anonymousUser = authenticateUserUseCase.authenticateAnonymous();
        
        // Generate JWT token for anonymous user
        String token = jwtService.generateToken(anonymousUser);
        long expiresIn = jwtService.getExpirationTime();
        
        // Create response with anonymous user details
        UserResponseDTO userDTO = UserDTOMapper.toResponseDTO(anonymousUser);
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(token, expiresIn, userDTO);
        
        return ResponseEntity.ok(responseDTO);
    }
}
