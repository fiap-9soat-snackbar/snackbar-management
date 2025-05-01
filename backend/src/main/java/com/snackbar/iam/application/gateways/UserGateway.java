package com.snackbar.iam.application.gateways;

import com.snackbar.iam.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Gateway interface for user persistence operations.
 * This interface defines how the application layer interacts with the persistence layer.
 */
public interface UserGateway {
    /**
     * Creates a new user in the persistence store.
     *
     * @param user The user to create
     * @return The created user with generated ID
     */
    User createUser(User user);

    /**
     * Updates an existing user in the persistence store.
     *
     * @param user The user to update
     * @return The updated user
     */
    User updateUser(User user);

    /**
     * Finds a user by their CPF.
     *
     * @param cpf The CPF to search for
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findByCpf(String cpf);

    /**
     * Finds a user by their email.
     *
     * @param email The email to search for
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their ID.
     *
     * @param id The ID to search for
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findById(String id);

    /**
     * Retrieves all users from the persistence store.
     *
     * @return A list of all users
     */
    List<User> findAll();

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete
     */
    void deleteById(String id);
}
