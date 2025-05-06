package com.snackbar.iam.application.adapter;

import com.snackbar.iam.application.ports.in.DeleteUserInputPort;
import com.snackbar.iam.application.ports.in.GetAllUsersInputPort;
import com.snackbar.iam.application.ports.in.GetUserByCpfInputPort;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.domain.adapter.UserEntityAdapter;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.infrastructure.UserRepository;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter that implements the legacy UserService functionality
 * while using the new clean architecture components.
 */
@Component("userServiceAdapter")
public class UserServiceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceAdapter.class);

    protected final IamRepository iamRepository;
    protected final UserRepository userRepository;
    private final GetAllUsersInputPort getAllUsersUseCase;
    private final GetUserByCpfInputPort getUserByCpfUseCase;
    private final DeleteUserInputPort deleteUserUseCase;
    private final UserEntityAdapter userEntityAdapter;

    public UserServiceAdapter(
            @Qualifier("iamRepositoryAdapter") IamRepository iamRepository,
            @Qualifier("userRepositoryAdapter") UserRepository userRepository,
            GetAllUsersInputPort getAllUsersUseCase,
            GetUserByCpfInputPort getUserByCpfUseCase,
            DeleteUserInputPort deleteUserUseCase,
            UserEntityAdapter userEntityAdapter
    ) {
        this.iamRepository = iamRepository;
        this.userRepository = userRepository;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.getUserByCpfUseCase = getUserByCpfUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.userEntityAdapter = userEntityAdapter;
        logger.info("UserServiceAdapter initialized");
    }

    public List<UserEntity> allUsers() {
        logger.debug("Getting all users");
        
        // Get all users using the use case
        List<User> users = getAllUsersUseCase.getAllUsers();
        
        // Convert the domain entities to legacy entities
        return users.stream()
                .map(user -> userEntityAdapter.toUserEntity(user))
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserByCpf(String cpf) {
        logger.debug("Getting user by CPF: {}", cpf);
        
        // Get the user using the use case
        User user = getUserByCpfUseCase.getUserByCpf(cpf);
        
        // Convert the domain entity to a response DTO
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getRole()
        );
    }

    public void deleteUser(String id) {
        logger.debug("Deleting user with ID: {}", id);
        
        // Delete the user using the use case
        deleteUserUseCase.deleteUser(id);
    }
}
