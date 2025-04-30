package com.snackbar.iam.infrastructure.config;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.*;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.application.usecases.*;
import com.snackbar.iam.infrastructure.gateways.UserRepositoryGateway;
import com.snackbar.iam.infrastructure.persistence.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Core configuration class for IAM module business components.
 * 
 * This configuration class is responsible for wiring up the core business components
 * of the IAM module following clean architecture principles. It focuses on:
 * 
 * - Creating use case implementations
 * - Configuring gateways between application and infrastructure layers
 * - Setting up domain event publishers
 * 
 * This class does NOT handle security or authentication configuration, which are
 * managed by IamSecurityConfig and IamAuthenticationConfig respectively.
 */
@Configuration
public class IamConfig {

    /**
     * Creates the user gateway implementation.
     *
     * @param userRepository The Spring Data repository
     * @return The user gateway
     */
    @Bean
    public UserGateway userGateway(UserRepository userRepository) {
        return new UserRepositoryGateway(userRepository);
    }

    /**
     * Creates the register user use case.
     *
     * @param userGateway The user gateway
     * @param passwordEncoder The password encoder
     * @param eventPublisher The domain event publisher
     * @return The register user use case
     */
    @Bean
    public RegisterUserInputPort registerUserUseCase(
            UserGateway userGateway,
            PasswordEncoder passwordEncoder,
            IamDomainEventPublisher eventPublisher) {
        return new RegisterUserUseCase(userGateway, passwordEncoder, eventPublisher);
    }

    /**
     * Creates the authenticate user use case.
     *
     * @param userGateway The user gateway
     * @param passwordEncoder The password encoder
     * @return The authenticate user use case
     */
    @Bean
    public AuthenticateUserInputPort authenticateUserUseCase(
            UserGateway userGateway,
            PasswordEncoder passwordEncoder) {
        return new AuthenticateUserUseCase(userGateway, passwordEncoder);
    }

    /**
     * Creates the get all users use case.
     *
     * @param userGateway The user gateway
     * @return The get all users use case
     */
    @Bean
    public GetAllUsersInputPort getAllUsersUseCase(UserGateway userGateway) {
        return new GetAllUsersUseCase(userGateway);
    }

    /**
     * Creates the get user by CPF use case.
     *
     * @param userGateway The user gateway
     * @return The get user by CPF use case
     */
    @Bean
    public GetUserByCpfInputPort getUserByCpfUseCase(UserGateway userGateway) {
        return new GetUserByCpfUseCase(userGateway);
    }

    /**
     * Creates the delete user use case.
     *
     * @param userGateway The user gateway
     * @param eventPublisher The domain event publisher
     * @return The delete user use case
     */
    @Bean
    public DeleteUserInputPort deleteUserUseCase(
            UserGateway userGateway,
            IamDomainEventPublisher eventPublisher) {
        return new DeleteUserUseCase(userGateway, eventPublisher);
    }
}
