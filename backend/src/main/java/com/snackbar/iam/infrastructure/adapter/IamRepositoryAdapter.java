package com.snackbar.iam.infrastructure.adapter;

import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.domain.adapter.UserEntityAdapter;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.infrastructure.persistence.UserRepository;
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
 * Adapter for IamRepository that delegates to the new UserRepository.
 * This adapter converts between legacy UserEntity and new persistence UserEntity.
 */
@Component("iamRepositoryAdapter")
public class IamRepositoryAdapter implements IamRepository {
    private static final Logger logger = LoggerFactory.getLogger(IamRepositoryAdapter.class);

    private final UserRepository userRepository;
    private final UserEntityAdapter userEntityAdapter;
    private final PersistenceEntityAdapter persistenceEntityAdapter;

    public IamRepositoryAdapter(
            @Qualifier("userRepository") UserRepository userRepository,
            UserEntityAdapter userEntityAdapter,
            PersistenceEntityAdapter persistenceEntityAdapter
    ) {
        this.userRepository = userRepository;
        this.userEntityAdapter = userEntityAdapter;
        this.persistenceEntityAdapter = persistenceEntityAdapter;
        logger.info("IamRepositoryAdapter initialized");
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to legacy entity
                    return userEntityAdapter.toUserEntity(user);
                });
    }

    @Override
    public Optional<UserEntity> findByCpf(String cpf) {
        logger.debug("Finding user by CPF: {}", cpf);
        return userRepository.findByCpf(cpf)
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to legacy entity
                    return userEntityAdapter.toUserEntity(user);
                });
    }

    @Override
    public <S extends UserEntity> S save(S entity) {
        logger.debug("Saving user entity: {}", entity.getId());
        // Convert from legacy entity to domain entity
        User user = userEntityAdapter.toUser(entity);
        // Convert from domain entity to persistence entity
        com.snackbar.iam.infrastructure.persistence.UserEntity persistenceEntity = 
                persistenceEntityAdapter.toPersistenceEntity(user);
        // Save the persistence entity
        com.snackbar.iam.infrastructure.persistence.UserEntity savedEntity = 
                userRepository.save(persistenceEntity);
        // Convert back to domain entity
        User savedUser = persistenceEntityAdapter.toDomainEntity(savedEntity);
        // Convert back to legacy entity and return
        UserEntity resultEntity = userEntityAdapter.toUserEntity(savedUser);
        @SuppressWarnings("unchecked")
        S result = (S) resultEntity;
        return result;
    }

    @Override
    public <S extends UserEntity> List<S> saveAll(Iterable<S> entities) {
        logger.debug("Saving multiple user entities");
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        logger.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to legacy entity
                    return userEntityAdapter.toUserEntity(user);
                });
    }

    @Override
    public boolean existsById(String id) {
        logger.debug("Checking if user exists by ID: {}", id);
        return userRepository.existsById(id);
    }

    @Override
    public List<UserEntity> findAll() {
        logger.debug("Finding all users");
        return userRepository.findAll().stream()
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to legacy entity
                    return userEntityAdapter.toUserEntity(user);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserEntity> findAllById(Iterable<String> ids) {
        logger.debug("Finding users by IDs");
        return userRepository.findAllById(ids).stream()
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to legacy entity
                    return userEntityAdapter.toUserEntity(user);
                })
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        logger.debug("Counting users");
        return userRepository.count();
    }

    @Override
    public void deleteById(String id) {
        logger.debug("Deleting user by ID: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public void delete(UserEntity entity) {
        logger.debug("Deleting user: {}", entity.getId());
        userRepository.deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        logger.debug("Deleting users by IDs");
        userRepository.deleteAllById(ids);
    }

    @Override
    public void deleteAll(Iterable<? extends UserEntity> entities) {
        logger.debug("Deleting multiple users");
        List<String> ids = StreamSupport.stream(entities.spliterator(), false)
                .map(UserEntity::getId)
                .collect(Collectors.toList());
        userRepository.deleteAllById(ids);
    }

    @Override
    public void deleteAll() {
        logger.debug("Deleting all users");
        userRepository.deleteAll();
    }

    @Override
    public List<UserEntity> findAll(Sort sort) {
        logger.debug("Finding all users with sort");
        return userRepository.findAll(sort).stream()
                .map(persistenceEntity -> {
                    // Convert from persistence entity to domain entity
                    User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
                    // Convert from domain entity to legacy entity
                    return userEntityAdapter.toUserEntity(user);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserEntity> findAll(Pageable pageable) {
        logger.debug("Finding users with pagination");
        Page<com.snackbar.iam.infrastructure.persistence.UserEntity> page = userRepository.findAll(pageable);
        return page.map(persistenceEntity -> {
            // Convert from persistence entity to domain entity
            User user = persistenceEntityAdapter.toDomainEntity(persistenceEntity);
            // Convert from domain entity to legacy entity
            return userEntityAdapter.toUserEntity(user);
        });
    }

    @Override
    public <S extends UserEntity> S insert(S entity) {
        logger.debug("Inserting user entity: {}", entity.getId());
        return save(entity);
    }

    @Override
    public <S extends UserEntity> List<S> insert(Iterable<S> entities) {
        logger.debug("Inserting multiple user entities");
        return saveAll(entities);
    }

    @Override
    public <S extends UserEntity> Optional<S> findOne(Example<S> example) {
        logger.debug("Finding one user by example");
        throw new UnsupportedOperationException("findOne by example is not supported in the adapter");
    }

    @Override
    public <S extends UserEntity> List<S> findAll(Example<S> example) {
        logger.debug("Finding all users by example");
        throw new UnsupportedOperationException("findAll by example is not supported in the adapter");
    }

    @Override
    public <S extends UserEntity> List<S> findAll(Example<S> example, Sort sort) {
        logger.debug("Finding all users by example with sort");
        throw new UnsupportedOperationException("findAll by example with sort is not supported in the adapter");
    }

    @Override
    public <S extends UserEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        logger.debug("Finding users by example with pagination");
        throw new UnsupportedOperationException("findAll by example with pagination is not supported in the adapter");
    }

    @Override
    public <S extends UserEntity> long count(Example<S> example) {
        logger.debug("Counting users by example");
        throw new UnsupportedOperationException("count by example is not supported in the adapter");
    }

    @Override
    public <S extends UserEntity> boolean exists(Example<S> example) {
        logger.debug("Checking if user exists by example");
        throw new UnsupportedOperationException("exists by example is not supported in the adapter");
    }

    @Override
    public <S extends UserEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        logger.debug("Finding by example with query function");
        throw new UnsupportedOperationException("findBy with query function is not supported in the adapter");
    }
}
