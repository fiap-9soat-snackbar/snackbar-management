package com.snackbar.iam.application;

import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.infrastructure.UserRepository;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy user service.
 * 
 * @deprecated This class is maintained for backward compatibility and will be removed in future versions.
 */
@Service
@Deprecated
public class UserService {
    protected final IamRepository iamRepository;
    protected final UserRepository userRepository;

    public UserService(
            @Qualifier("iamRepositoryAdapter") IamRepository iamRepository, 
            @Qualifier("userRepositoryAdapter") UserRepository userRepository) {
        this.iamRepository = iamRepository;
        this.userRepository = userRepository;
    }

    public List<UserEntity> allUsers() {
        List<UserEntity> users = new ArrayList<>();

        iamRepository.findAll().forEach(users::add);

        return users;
    }

    public UserResponseDTO getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf).map(user ->
                new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCpf(),
                        user.getRole()
                )
        ).orElseThrow();
    }

    public void deleteUser(String id) {
        iamRepository.deleteById(id);
    }
}
