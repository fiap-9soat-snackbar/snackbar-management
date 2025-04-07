package com.snackbar.iam.infrastructure;

import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IamRepository extends MongoRepository<UserEntity, String> {

    Optional<UserDetailsEntity> findByEmail(String email);

    Optional<UserDetailsEntity> findByCpf(String cpf);

}
