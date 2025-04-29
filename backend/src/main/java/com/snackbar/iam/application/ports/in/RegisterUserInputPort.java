package com.snackbar.iam.application.ports.in;

import com.snackbar.iam.domain.entity.User;

/**
 * Input port for registering a new user in the system.
 * This interface defines the use case for user registration.
 */
public interface RegisterUserInputPort {
    /**
     * Registers a new user in the system.
     *
     * @param user The user to register
     * @return The registered user with generated ID
     * @throws com.snackbar.iam.domain.exceptions.DuplicateUserException if a user with the same CPF or email already exists
     * @throws com.snackbar.iam.domain.exceptions.InvalidUserDataException if the user data is invalid
     */
    User registerUser(User user);
}
