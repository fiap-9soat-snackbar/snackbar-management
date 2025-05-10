package com.snackbar.iam.infrastructure.config;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.*;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.application.usecases.*;
import com.snackbar.iam.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IamConfigTest {

    @InjectMocks
    private IamConfig iamConfig;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IamDomainEventPublisher eventPublisher;

    @Test
    @DisplayName("Should create UserGateway bean")
    void shouldCreateUserGatewayBean() {
        // When
        UserGateway userGateway = iamConfig.userGateway(userRepository);
        
        // Then
        assertNotNull(userGateway);
    }

    @Test
    @DisplayName("Should create RegisterUserInputPort bean")
    void shouldCreateRegisterUserInputPortBean() {
        // Given
        UserGateway userGateway = iamConfig.userGateway(userRepository);
        
        // When
        RegisterUserInputPort registerUserInputPort = iamConfig.registerUserUseCase(userGateway, passwordEncoder, eventPublisher);
        
        // Then
        assertNotNull(registerUserInputPort);
        assertTrue(registerUserInputPort instanceof RegisterUserUseCase);
    }

    @Test
    @DisplayName("Should create AuthenticateUserInputPort bean")
    void shouldCreateAuthenticateUserInputPortBean() {
        // Given
        UserGateway userGateway = iamConfig.userGateway(userRepository);
        
        // When
        AuthenticateUserInputPort authenticateUserInputPort = iamConfig.authenticateUserUseCase(userGateway, passwordEncoder);
        
        // Then
        assertNotNull(authenticateUserInputPort);
        assertTrue(authenticateUserInputPort instanceof AuthenticateUserUseCase);
    }

    @Test
    @DisplayName("Should create GetAllUsersInputPort bean")
    void shouldCreateGetAllUsersInputPortBean() {
        // Given
        UserGateway userGateway = iamConfig.userGateway(userRepository);
        
        // When
        GetAllUsersInputPort getAllUsersInputPort = iamConfig.getAllUsersUseCase(userGateway);
        
        // Then
        assertNotNull(getAllUsersInputPort);
        assertTrue(getAllUsersInputPort instanceof GetAllUsersUseCase);
    }

    @Test
    @DisplayName("Should create GetUserByCpfInputPort bean")
    void shouldCreateGetUserByCpfInputPortBean() {
        // Given
        UserGateway userGateway = iamConfig.userGateway(userRepository);
        
        // When
        GetUserByCpfInputPort getUserByCpfInputPort = iamConfig.getUserByCpfUseCase(userGateway);
        
        // Then
        assertNotNull(getUserByCpfInputPort);
        assertTrue(getUserByCpfInputPort instanceof GetUserByCpfUseCase);
    }

    @Test
    @DisplayName("Should create UpdateUserInputPort bean")
    void shouldCreateUpdateUserInputPortBean() {
        // Given
        UserGateway userGateway = iamConfig.userGateway(userRepository);
        
        // When
        UpdateUserInputPort updateUserInputPort = iamConfig.updateUserUseCase(userGateway, passwordEncoder, eventPublisher);
        
        // Then
        assertNotNull(updateUserInputPort);
        assertTrue(updateUserInputPort instanceof UpdateUserUseCase);
    }

    @Test
    @DisplayName("Should create DeleteUserInputPort bean")
    void shouldCreateDeleteUserInputPortBean() {
        // Given
        UserGateway userGateway = iamConfig.userGateway(userRepository);
        
        // When
        DeleteUserInputPort deleteUserInputPort = iamConfig.deleteUserUseCase(userGateway, eventPublisher);
        
        // Then
        assertNotNull(deleteUserInputPort);
        assertTrue(deleteUserInputPort instanceof DeleteUserUseCase);
    }
}
