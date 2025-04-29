package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.application.ports.in.GetUserByCpfInputPort;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;

/**
 * Implementation of the GetUserByCpfInputPort that handles retrieving a user by CPF.
 */
public class GetUserByCpfUseCase implements GetUserByCpfInputPort {
    private final UserGateway userGateway;

    public GetUserByCpfUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public User getUserByCpf(String cpf) {
        return userGateway.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("User not found with CPF: " + cpf));
    }
}
