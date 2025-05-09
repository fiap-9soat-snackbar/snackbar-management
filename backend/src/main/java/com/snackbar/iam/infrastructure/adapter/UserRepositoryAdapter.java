package com.snackbar.iam.infrastructure.adapter;

import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.gateways.UserEntityMapper;
import com.snackbar.iam.infrastructure.persistence.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter that provides legacy UserRepository functionality.
 * This adapter converts between legacy UserEntity and new persistence UserEntity.
 */
@Component("userRepositoryAdapter")
public class UserRepositoryAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryAdapter.class);

    private final com.snackbar.iam.infrastructure.persistence.UserRepository userRepository;
    private final PersistenceEntityAdapter persistenceEntityAdapter;

    public UserRepositoryAdapter(
            @Qualifier("userRepository") com.snackbar.iam.infrastructure.persistence.UserRepository userRepository,
            PersistenceEntityAdapter persistenceEntityAdapter
    ) {
        this.userRepository = userRepository;
        this.persistenceEntityAdapter = persistenceEntityAdapter;
        logger.info("UserRepositoryAdapter initialized");
    }

    public Optional<UserEntity> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to persistence entity using mapper
                    return UserEntityMapper.toEntity(user);
                });
    }

    public Optional<UserEntity> findByCpf(String cpf) {
        logger.debug("Finding user by CPF: {}", cpf);
        return userRepository.findByCpf(cpf)
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to persistence entity using mapper
                    return UserEntityMapper.toEntity(user);
                });
    }

    public <S extends UserEntity> S save(S entity) {
        logger.debug("Saving user entity: {}", entity.getId());
        // Convert from persistence entity to domain entity using mapper
        User user = UserEntityMapper.toDomain(entity);
        // Convert from domain entity to persistence entity
        com.snackbar.iam.infrastructure.persistence.UserEntity persistenceEntity = 
                persistenceEntityAdapter.toPersistenceEntity(user);
        // Save the persistence entity
        com.snackbar.iam.infrastructure.persistence.UserEntity savedEntity = 
                userRepository.save(persistenceEntity);
        // Convert back to domain entity
        User savedUser = persistenceEntityAdapter.toDomainEntity(savedEntity);
        // Convert back to persistence entity and return
        UserEntity resultEntity = UserEntityMapper.toEntity(savedUser);
        @SuppressWarnings("unchecked")
        S result = (S) resultEntity;
        return result;
    }

    public <S extends UserEntity> List<S> saveAll(Iterable<S> entities) {
        logger.debug("Saving multiple user entities");
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::save)
                .collect(Collectors.toList());
    }

    public Optional<UserEntity> findById(String id) {
        logger.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to persistence entity using mapper
                    return UserEntityMapper.toEntity(user);
                });
    }

    public boolean existsById(String id) {
        logger.debug("Checking if user exists by ID: {}", id);
        return userRepository.existsById(id);
    }

    public List<UserEntity> findAll() {
        logger.debug("Finding all users");
        return userRepository.findAll().stream()
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to persistence entity using mapper
                    return UserEntityMapper.toEntity(user);
                })
                .collect(Collectors.toList());
    }

    public List<UserEntity> findAllById(Iterable<String> ids) {
        logger.debug("Finding users by IDs");
        return userRepository.findAllById(ids).stream()
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to persistence entity using mapper
                    return UserEntityMapper.toEntity(user);
                })
                .collect(Collectors.toList());
    }

    public long count() {
        logger.debug("Counting users");
        return userRepository.count();
    }

    public void deleteById(String id) {
        logger.debug("Deleting user by ID: {}", id);
        userRepository.deleteById(id);
    }

    public void delete(UserEntity entity) {
        logger.debug("Deleting user: {}", entity.getId());
        userRepository.deleteById(entity.getId());
    }

    public void deleteAllById(Iterable<? extends String> ids) {
        logger.debug("Deleting users by IDs");
        userRepository.deleteAllById(ids);
    }

    public void deleteAll(Iterable<? extends UserEntity> entities) {
        logger.debug("Deleting multiple users");
        List<String> ids = StreamSupport.stream(entities.spliterator(), false)
                .map(UserEntity::getId)
                .collect(Collectors.toList());
        userRepository.deleteAllById(ids);
    }

    public void deleteAll() {
        logger.debug("Deleting all users");
        userRepository.deleteAll();
    }

    public List<UserEntity> findAll(Sort sort) {
        logger.debug("Finding all users with sort");
        return userRepository.findAll(sort).stream()
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to persistence entity using mapper
                    return UserEntityMapper.toEntity(user);
                })
                .collect(Collectors.toList());
    }

    public Page<UserEntity> findAll(Pageable pageable) {
        logger.debug("Finding users with pagination");
        Page<com.snackbar.iam.infrastructure.persistence.UserEntity> page = userRepository.findAll(pageable);
        return page.map(persistenceEntity -> {
            // Convert from persistence entity to domain entity
            User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
            // Convert from domain entity to persistence entity using mapper
            return UserEntityMapper.toEntity(user);
        });
    }

    public <S extends UserEntity> S insert(S entity) {
        logger.debug("Inserting user entity: {}", entity.getId());
        return save(entity);
    }

    public <S extends UserEntity> List<S> insert(Iterable<S> entities) {
        logger.debug("Inserting multiple user entities");
        return saveAll(entities);
    }

    public <S extends UserEntity> Optional<S> findOne(Example<S> example) {
        logger.debug("Finding one user by example");
        throw new UnsupportedOperationException("findOne by example is not supported in the adapter");
    }

    public <S extends UserEntity> List<S> findAll(Example<S> example) {
        logger.debug("Finding all users by example");
        throw new UnsupportedOperationException("findAll by example is not supported in the adapter");
    }

    public <S extends UserEntity> List<S> findAll(Example<S> example, Sort sort) {
        logger.debug("Finding all users by example with sort");
        throw new UnsupportedOperationException("findAll by example with sort is not supported in the adapter");
    }

    public <S extends UserEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        logger.debug("Finding users by example with pagination");
        throw new UnsupportedOperationException("findAll by example with pagination is not supported in the adapter");
    }

    public <S extends UserEntity> long count(Example<S> example) {
        logger.debug("Counting users by example");
        throw new UnsupportedOperationException("count by example is not supported in the adapter");
    }

    public <S extends UserEntity> boolean exists(Example<S> example) {
        logger.debug("Checking if user exists by example");
        throw new UnsupportedOperationException("exists by example is not supported in the adapter");
    }

    public <S extends UserEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        logger.debug("Finding by example with query function");
        throw new UnsupportedOperationException("findBy with query function is not supported in the adapter");
    }
}
