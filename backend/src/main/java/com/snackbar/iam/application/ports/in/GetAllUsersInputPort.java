package com.snackbar.iam.application.ports.in;

import com.snackbar.iam.domain.entity.User;

import java.util.List;

/**
 * Input port for retrieving all users in the system.
 * This interface defines the use case for listing all users.
 */
public interface GetAllUsersInputPort {
    /**
     * Retrieves all users in the system.
     *
     * @return A list of all users
     */
    List<User> getAllUsers();
}
