package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.AuthenticateUserInputPort;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.InvalidCredentialsException;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementation of the AuthenticateUserInputPort that handles user authentication.
 */
public class AuthenticateUserUseCase implements AuthenticateUserInputPort {
    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    public AuthenticateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User authenticate(String cpf, String password) {
        // Find user by CPF
        User user = userGateway.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("User not found with CPF: " + cpf));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials for user with CPF: " + cpf);
        }

        return user;
    }
    
    @Override
    public User authenticateAnonymous() {
        // Create an anonymous user with CONSUMER role
        // Note: We're using null for validation-required fields since this is a special case
        // The password is null as anonymous users don't have passwords
        try {
            return new User(
                    "anonymous", // ID
                    "Anonymous User", // Name
                    "anonymous@example.com", // Email (placeholder)
                    "anonymous", // CPF
                    IamRole.CONSUMER, // Role
                    null // Password
            );
        } catch (Exception e) {
            // If validation fails, create a minimal anonymous user
            // This is a fallback in case the domain validation is strict
            User anonymousUser = new User();
            anonymousUser.setId("anonymous");
            anonymousUser.setName("Anonymous User");
            anonymousUser.setCpf("anonymous");
            anonymousUser.setRole(IamRole.CONSUMER);
            return anonymousUser;
        }
    }
}
