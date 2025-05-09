package com.snackbar.iam.application.adapter;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.AuthenticateUserInputPort;
import com.snackbar.iam.application.ports.in.RegisterUserInputPort;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.controllers.dto.LoginRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
import com.snackbar.iam.infrastructure.gateways.UserEntityMapper;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import com.snackbar.iam.infrastructure.security.UserDetailsAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements the legacy AuthenticationService functionality
 * while using the new clean architecture components.
 */
@Component("authenticationServiceAdapter")
public class AuthenticationServiceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceAdapter.class);

    protected final UserGateway userGateway;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationManager authenticationManager;
    private final RegisterUserInputPort registerUserUseCase;
    private final AuthenticateUserInputPort authenticateUserUseCase;
    
    @Autowired
    private AuthenticationManager authManager;

    public AuthenticationServiceAdapter(
            @Qualifier("userRepositoryGateway") UserGateway userGateway,
            @Qualifier("legacyAuthenticationManager") AuthenticationManager authenticationManager,
            @Qualifier("legacyPasswordEncoder") PasswordEncoder passwordEncoder,
            RegisterUserInputPort registerUserUseCase,
            AuthenticateUserInputPort authenticateUserUseCase
    ) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        logger.info("AuthenticationServiceAdapter initialized");
    }

    public UserEntity signup(RegisterUserRequestDTO input) {
        logger.debug("Signing up user with email: {}", input.email());
        
        // Create a User domain entity from the DTO
        User user = new User(
                null, // ID will be generated
                input.fullName(),
                input.email(),
                input.cpf(),
                input.role(),
                input.password()
        );
        
        // Register the user using the use case
        User registeredUser = registerUserUseCase.registerUser(user);
        
        // Convert the domain entity to a persistence entity using the mapper
        return UserEntityMapper.toEntity(registeredUser);
    }

    public UserDetails authenticate(LoginRequestDTO input) {
        logger.debug("Authenticating user with CPF: {}", input.cpf());
        
        // Authenticate using Spring Security (required for session management)
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.cpf(),
                        input.password()
                )
        );
        
        // Authenticate using the use case
        User authenticatedUser = authenticateUserUseCase.authenticate(input.cpf(), input.password());
        
        // Return UserDetailsAdapter instead of UserDetailsEntity
        return new UserDetailsAdapter(authenticatedUser);
    }

    public UserDetails findByCpf(String cpf) {
        logger.debug("Finding user by CPF: {}", cpf);
        
        // Use the authenticate use case to find the user
        User user = authenticateUserUseCase.authenticate(cpf, null);
        
        // Return UserDetailsAdapter instead of UserDetailsEntity
        return new UserDetailsAdapter(user);
    }
}
