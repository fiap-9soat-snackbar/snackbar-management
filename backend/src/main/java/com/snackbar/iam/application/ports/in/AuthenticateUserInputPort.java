package com.snackbar.iam.application.ports.in;

import com.snackbar.iam.domain.entity.User;

/**
 * Input port for authenticating a user in the system.
 * This interface defines the use case for user authentication.
 */
public interface AuthenticateUserInputPort {
    /**
     * Authenticates a user with the given CPF and password.
     *
     * @param cpf The user's CPF
     * @param password The user's password
     * @return The authenticated user
     * @throws com.snackbar.iam.domain.exceptions.UserNotFoundException if no user with the given CPF exists
     * @throws com.snackbar.iam.domain.exceptions.InvalidCredentialsException if the password is incorrect
     */
    User authenticateUser(String cpf, String password);
}
