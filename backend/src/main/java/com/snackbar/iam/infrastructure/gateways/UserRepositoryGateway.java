package com.snackbar.iam.infrastructure.gateways;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import com.snackbar.iam.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the UserGateway interface that uses Spring Data MongoDB.
 * This class adapts between the domain and persistence layers.
 * Marked as @Primary to be preferred over legacy repositories.
 */
@Component("userRepositoryGateway")
@Primary
public class UserRepositoryGateway implements UserGateway {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryGateway.class);
    
    private final UserRepository userRepository;
    
    public UserRepositoryGateway(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("UserRepositoryGateway initialized");
    }
    
    @Override
    public User createUser(User user) {
        UserEntity entity = UserEntityMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return UserEntityMapper.toDomain(savedEntity);
    }
    
    @Override
    public User updateUser(User user) {
        // Find the existing entity to ensure it exists
        Optional<UserEntity> existingEntity = userRepository.findById(user.getId());
        if (existingEntity.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + user.getId());
        }
        
        // Update the entity
        UserEntity entityToUpdate = UserEntityMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entityToUpdate);
        return UserEntityMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findByCpf(String cpf) {
        return userRepository.findByCpf(cpf)
                .map(UserEntityMapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntityMapper::toDomain);
    }
    
    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id)
                .map(UserEntityMapper::toDomain);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(UserEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}
