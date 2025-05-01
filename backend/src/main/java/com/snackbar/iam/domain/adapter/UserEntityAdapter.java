package com.snackbar.iam.domain.adapter;

import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter class that converts between legacy UserDetailsEntity and new User domain entity.
 * This adapter facilitates the transition from the legacy domain model to the new clean architecture model.
 */
@Component
public class UserEntityAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UserEntityAdapter.class);

    /**
     * Converts a legacy UserDetailsEntity to a new User domain entity.
     *
     * @param userDetailsEntity The legacy entity to convert
     * @return A new User domain entity with the same data
     */
    public User toUser(UserDetailsEntity userDetailsEntity) {
        if (userDetailsEntity == null) {
            return null;
        }

        logger.debug("Converting UserDetailsEntity to User: {}", userDetailsEntity.getId());
        
        return new User(
            userDetailsEntity.getId(),
            userDetailsEntity.getName(),
            userDetailsEntity.getEmail(),
            userDetailsEntity.getCpf(),
            userDetailsEntity.getRole(),
            userDetailsEntity.getPassword()
        );
    }

    /**
     * Converts a new User domain entity to a legacy UserDetailsEntity.
     *
     * @param user The new domain entity to convert
     * @return A legacy UserDetailsEntity with the same data
     */
    public UserDetailsEntity toUserDetailsEntity(User user) {
        if (user == null) {
            return null;
        }

        logger.debug("Converting User to UserDetailsEntity: {}", user.getId());
        
        return UserDetailsEntity.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .cpf(user.getCpf())
            .role(user.getRole())
            .password(user.getPassword())
            .build();
    }

    /**
     * Updates an existing UserDetailsEntity with data from a User domain entity.
     * This is useful for updating entities without creating new instances.
     *
     * @param existingEntity The existing entity to update
     * @param user The user domain entity containing the new data
     * @return The updated UserDetailsEntity
     */
    public UserDetailsEntity updateUserDetailsEntity(UserDetailsEntity existingEntity, User user) {
        if (existingEntity == null || user == null) {
            return existingEntity;
        }

        logger.debug("Updating UserDetailsEntity with User data: {}", user.getId());
        
        existingEntity.setName(user.getName());
        existingEntity.setEmail(user.getEmail());
        existingEntity.setCpf(user.getCpf());
        existingEntity.setRole(user.getRole());
        existingEntity.setPassword(user.getPassword());
        
        return existingEntity;
    }
}
