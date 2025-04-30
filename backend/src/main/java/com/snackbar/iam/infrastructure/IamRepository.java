package com.snackbar.iam.infrastructure;

import com.snackbar.iam.domain.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Legacy IAM repository interface.
 * This is kept for backward compatibility during refactoring.
 * 
 * @deprecated Use {@link com.snackbar.iam.infrastructure.persistence.UserRepository} instead.
 */
@Repository("legacyIamRepository")
@Deprecated
public interface IamRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByCpf(String cpf);

}
