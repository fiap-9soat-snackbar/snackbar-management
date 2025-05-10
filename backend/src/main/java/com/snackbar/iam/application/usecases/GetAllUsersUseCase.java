package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.GetAllUsersInputPort;
import com.snackbar.iam.domain.entity.User;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of the GetAllUsersInputPort that handles retrieving all users.
 */
public class GetAllUsersUseCase implements GetAllUsersInputPort {
    private final UserGateway userGateway;

    public GetAllUsersUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userGateway.findAll();
        return users != null ? users : Collections.emptyList();
    }
}
