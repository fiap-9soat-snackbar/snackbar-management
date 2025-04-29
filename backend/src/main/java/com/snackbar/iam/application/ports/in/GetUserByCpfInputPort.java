package com.snackbar.iam.application.ports.in;

import com.snackbar.iam.domain.entity.User;

/**
 * Input port for retrieving a user by CPF.
 * This interface defines the use case for finding a user by their CPF.
 */
public interface GetUserByCpfInputPort {
    /**
     * Retrieves a user by their CPF.
     *
     * @param cpf The CPF of the user to retrieve
     * @return The user with the given CPF
     * @throws com.snackbar.iam.domain.exceptions.UserNotFoundException if no user with the given CPF exists
     */
    User getUserByCpf(String cpf);
}
