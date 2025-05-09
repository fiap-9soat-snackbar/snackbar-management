package com.snackbar.iam.domain.adapter;

import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import com.snackbar.iam.infrastructure.security.UserDetailsAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Adapter class that converts between persistence entities and domain entities.
 * This adapter facilitates the transition from the legacy domain model to the new clean architecture model.
 * 
 * @deprecated This adapter will be removed once the migration to clean architecture is complete.
 * Direct use of domain entities and mappers is preferred.
 */
@Component
public class UserEntityAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UserEntityAdapter.class);

    /**
     * Converts a persistence UserEntity to a domain User entity.
     *
     * @param userEntity The persistence entity to convert
     * @return A domain User entity with the same data
     */
    public User toUser(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        logger.debug("Converting persistence UserEntity to User: {}", userEntity.getId());
        
        return new User(
            userEntity.getId(),
            userEntity.getName(),
            userEntity.getEmail(),
            userEntity.getCpf(),
            userEntity.getRole(),
            userEntity.getPassword()
        );
    }

    /**
     * Converts a new User domain entity to a UserDetails implementation.
     *
     * @param user The new domain entity to convert
     * @return A UserDetails implementation with the same data
     */
    public UserDetails toUserDetails(User user) {
        if (user == null) {
            return null;
        }

        logger.debug("Converting User to UserDetails: {}", user.getId());
        
        return new UserDetailsAdapter(user);
    }

    /**
     * Converts a domain User entity to a persistence UserEntity.
     *
     * @param user The domain entity to convert
     * @return A persistence UserEntity with the same data
     */
    public UserEntity toUserEntity(User user) {
        if (user == null) {
            return null;
        }

        logger.debug("Converting User to persistence UserEntity: {}", user.getId());
        
        return UserEntity.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .cpf(user.getCpf())
            .role(user.getRole())
            .password(user.getPassword())
            .build();
    }

    /**
     * Updates an existing persistence UserEntity with data from a domain User entity.
     * This is useful for updating entities without creating new instances.
     *
     * @param existingEntity The existing persistence entity to update
     * @param user The domain User entity containing the new data
     * @return The updated persistence UserEntity
     */
    public UserEntity updateUserEntity(UserEntity existingEntity, User user) {
        if (existingEntity == null || user == null) {
            return existingEntity;
        }

        logger.debug("Updating persistence UserEntity with User data: {}", user.getId());
        
        existingEntity.setName(user.getName());
        existingEntity.setEmail(user.getEmail());
        existingEntity.setCpf(user.getCpf());
        existingEntity.setRole(user.getRole());
        existingEntity.setPassword(user.getPassword());
        
        return existingEntity;
    }
}
