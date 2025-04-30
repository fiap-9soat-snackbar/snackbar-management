package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.RegisterUserInputPort;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserCreatedEvent;
import com.snackbar.iam.domain.exceptions.DuplicateUserException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementation of the RegisterUserInputPort that handles user registration.
 */
public class RegisterUserUseCase implements RegisterUserInputPort {
    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final IamDomainEventPublisher eventPublisher;

    public RegisterUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder, IamDomainEventPublisher eventPublisher) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User registerUser(User user) {
        // Check if user with same CPF already exists
        userGateway.findByCpf(user.getCpf()).ifPresent(existingUser -> {
            throw new DuplicateUserException("User with CPF " + user.getCpf() + " already exists");
        });

        // Check if user with same email already exists
        userGateway.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            throw new DuplicateUserException("User with email " + user.getEmail() + " already exists");
        });

        // Encode the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Create the user
        User createdUser = userGateway.createUser(user);

        // Publish domain event
        eventPublisher.publish(new UserCreatedEvent(createdUser));

        return createdUser;
    }
}
