package com.snackbar.iam.infrastructure.persistence;

import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for UserEntity.
 * Marked as @Primary to be preferred over the legacy repository during dependency injection.
 */
@Repository("userRepository")
@Primary
public interface UserRepository extends MongoRepository<UserEntity, String> {
    /**
     * Find a user by email.
     *
     * @param email The email to search for
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Find a user by CPF.
     *
     * @param cpf The CPF to search for
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<UserEntity> findByCpf(String cpf);
}
