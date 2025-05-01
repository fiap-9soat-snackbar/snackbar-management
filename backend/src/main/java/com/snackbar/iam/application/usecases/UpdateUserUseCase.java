package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.UpdateUserInputPort;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserUpdatedEvent;
import com.snackbar.iam.domain.exceptions.DuplicateUserException;
import com.snackbar.iam.domain.exceptions.InvalidUserDataException;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Implementation of the UpdateUserInputPort that handles user updates.
 * Following PUT semantics, this implementation expects all fields to be provided.
 */
public class UpdateUserUseCase implements UpdateUserInputPort {
    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final IamDomainEventPublisher eventPublisher;

    public UpdateUserUseCase(UserGateway userGateway, PasswordEncoder passwordEncoder, IamDomainEventPublisher eventPublisher) {
        this.userGateway = userGateway;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User updateUser(String id, User updatedUser) {
        // Find the existing user
        User existingUser = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        // Validate that CPF matches the existing user (CPF cannot be changed)
        if (!existingUser.getCpf().equals(updatedUser.getCpf())) {
            throw new InvalidUserDataException("CPF cannot be changed");
        }

        // Check if email is being changed and if it's already in use by another user
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            Optional<User> userWithSameEmail = userGateway.findByEmail(updatedUser.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new DuplicateUserException("User with email " + updatedUser.getEmail() + " already exists");
            }
        }

        // Create a new user object with the updated fields and the existing ID
        User userToUpdate = new User(
            id,
            updatedUser.getName(),
            updatedUser.getEmail(),
            updatedUser.getCpf(),
            updatedUser.getRole(),
            existingUser.getPassword() // Start with existing password
        );
        
        // Only update password if it's provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            userToUpdate.setPassword(encodedPassword);
        }

        // Update the user
        User savedUser = userGateway.updateUser(userToUpdate);

        // Publish domain event
        eventPublisher.publish(new UserUpdatedEvent(savedUser));

        return savedUser;
    }
}
