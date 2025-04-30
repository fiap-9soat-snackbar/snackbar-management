package com.snackbar.iam.infrastructure.gateways;

import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.persistence.UserEntity;

/**
 * Mapper class to convert between domain User and persistence UserEntity.
 */
public class UserEntityMapper {
    
    /**
     * Maps a domain User to a persistence UserEntity.
     *
     * @param user The domain User to map
     * @return The mapped UserEntity
     */
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        
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
     * Maps a persistence UserEntity to a domain User.
     *
     * @param entity The UserEntity to map
     * @return The mapped domain User
     */
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getRole(),
                entity.getPassword()
        );
    }
}
