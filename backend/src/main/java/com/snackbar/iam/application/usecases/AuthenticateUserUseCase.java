package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.AuthenticateUserInputPort;
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
    public User authenticateUser(String cpf, String password) {
        // Find user by CPF
        User user = userGateway.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("User not found with CPF: " + cpf));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials for user with CPF: " + cpf);
        }

        return user;
    }
}
