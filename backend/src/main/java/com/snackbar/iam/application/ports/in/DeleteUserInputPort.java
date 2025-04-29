package com.snackbar.iam.application.ports.in;

/**
 * Input port for deleting a user from the system.
 * This interface defines the use case for user deletion.
 */
public interface DeleteUserInputPort {
    /**
     * Deletes a user with the given ID.
     *
     * @param id The ID of the user to delete
     * @throws com.snackbar.iam.domain.exceptions.UserNotFoundException if no user with the given ID exists
     */
    void deleteUser(String id);
}
