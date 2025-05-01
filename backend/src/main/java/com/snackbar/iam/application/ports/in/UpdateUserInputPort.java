package com.snackbar.iam.application.ports.in;

import com.snackbar.iam.domain.entity.User;

/**
 * Input port for updating a user.
 * This interface defines the contract for updating user information.
 */
public interface UpdateUserInputPort {
    /**
     * Updates a user with the provided information.
     *
     * @param id The ID of the user to update
     * @param user The updated user information
     * @return The updated user
     */
    User updateUser(String id, User user);
}
