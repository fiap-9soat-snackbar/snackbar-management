package com.snackbar.iam.application;

import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.infrastructure.UserRepository;
import com.snackbar.iam.web.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final IamRepository iamRepository;
    private final UserRepository userRepository;

    public UserService(IamRepository iamRepository, UserRepository userRepository) {
        this.iamRepository = iamRepository;
        this.userRepository = userRepository;
    }

    public List<UserEntity> allUsers() {
        List<UserEntity> users = new ArrayList<>();

        iamRepository.findAll().forEach(users::add);

        return users;
    }

    public UserResponse getUserByCpf(String cpf) {
        return userRepository.findByCpf(cpf).map(user ->
                UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .cpf(user.getCpf())
                        .email(user.getEmail())
                        .role(user.getRole())
                    .build()
        ).orElseThrow();
    }

    public void deleteUser(String id) {
        iamRepository.deleteById(id);
    }
}
