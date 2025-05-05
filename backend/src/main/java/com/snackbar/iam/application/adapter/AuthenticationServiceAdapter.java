package com.snackbar.iam.application.adapter;

import com.snackbar.iam.application.AuthenticationService;
import com.snackbar.iam.application.ports.in.AuthenticateUserInputPort;
import com.snackbar.iam.application.ports.in.RegisterUserInputPort;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.domain.adapter.UserEntityAdapter;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.infrastructure.controllers.dto.LoginRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter for AuthenticationService that delegates to the new use cases.
 * This adapter maintains backward compatibility while using the new clean architecture components.
 */
@Component
public class AuthenticationServiceAdapter extends AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceAdapter.class);

    private final RegisterUserInputPort registerUserUseCase;
    private final AuthenticateUserInputPort authenticateUserUseCase;
    private final UserEntityAdapter userEntityAdapter;
    
    @Autowired
    private AuthenticationManager authManager;

    public AuthenticationServiceAdapter(
            @Qualifier("iamRepositoryAdapter") IamRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RegisterUserInputPort registerUserUseCase,
            AuthenticateUserInputPort authenticateUserUseCase,
            UserEntityAdapter userEntityAdapter
    ) {
        super(userRepository, authenticationManager, passwordEncoder);
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.userEntityAdapter = userEntityAdapter;
        logger.info("AuthenticationServiceAdapter initialized");
    }

    @Override
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
        
        // Convert the domain entity back to a legacy entity
        return userEntityAdapter.toUserEntity(registeredUser);
    }

    @Override
    public UserDetailsEntity authenticate(LoginRequestDTO input) {
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
        
        // Convert the domain entity to a legacy entity
        return userEntityAdapter.toUserDetailsEntity(authenticatedUser);
    }

    @Override
    public UserDetailsEntity findByCpf(String cpf) {
        logger.debug("Finding user by CPF: {}", cpf);
        
        // Use the authenticate use case to find the user
        User user = authenticateUserUseCase.authenticate(cpf, null);
        
        // Convert the domain entity to a legacy entity
        return userEntityAdapter.toUserDetailsEntity(user);
    }
}
