package com.snackbar.iam.infrastructure.adapter;

import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter for converting between domain User entity and persistence UserEntity.
 */
@Component
public class PersistenceEntityAdapter {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceEntityAdapter.class);

    /**
     * Converts a domain User entity to a persistence UserEntity.
     *
     * @param user The domain User entity to convert
     * @return A persistence UserEntity with the same data
     */
    public UserEntity toPersistenceEntity(User user) {
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
     * Converts a persistence UserEntity to a domain User entity.
     *
     * @param userEntity The persistence UserEntity to convert
     * @return A domain User entity with the same data
     */
    public User toDomainEntity(UserEntity userEntity) {
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
     * Updates an existing persistence UserEntity with data from a domain User entity.
     *
     * @param existingEntity The existing persistence UserEntity to update
     * @param user The domain User entity containing the new data
     * @return The updated persistence UserEntity
     */
    public UserEntity updatePersistenceEntity(UserEntity existingEntity, User user) {
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
