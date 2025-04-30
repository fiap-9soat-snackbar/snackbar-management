package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.DeleteUserInputPort;
import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.event.UserDeletedEvent;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;

/**
 * Implementation of the DeleteUserInputPort that handles user deletion.
 */
public class DeleteUserUseCase implements DeleteUserInputPort {
    private final UserGateway userGateway;
    private final IamDomainEventPublisher eventPublisher;

    public DeleteUserUseCase(UserGateway userGateway, IamDomainEventPublisher eventPublisher) {
        this.userGateway = userGateway;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void deleteUser(String id) {
        // Find user by ID to ensure it exists and to get the CPF for the event
        User user = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        // Delete the user
        userGateway.deleteById(id);

        // Publish domain event
        eventPublisher.publish(new UserDeletedEvent(id, user.getCpf()));
    }
}
