package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.application.ports.in.DeleteUserInputPort;
import com.snackbar.iam.application.ports.in.GetAllUsersInputPort;
import com.snackbar.iam.application.ports.in.GetUserByCpfInputPort;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for user management operations.
 * Handles user retrieval and deletion.
 * 
 * Note: Using a temporary URL path during refactoring to avoid conflicts with existing controllers.
 * Will be changed back to /api/user/* once refactoring is complete.
 */
@RestController
@RequestMapping("/api/v2/user")
public class UserMgmtController {

    private final GetAllUsersInputPort getAllUsersUseCase;
    private final GetUserByCpfInputPort getUserByCpfUseCase;
    private final DeleteUserInputPort deleteUserUseCase;

    public UserMgmtController(
            GetAllUsersInputPort getAllUsersUseCase,
            GetUserByCpfInputPort getUserByCpfUseCase,
            DeleteUserInputPort deleteUserUseCase) {
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.getUserByCpfUseCase = getUserByCpfUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    /**
     * Endpoint for retrieving all users.
     *
     * @return ResponseEntity with list of all users
     */
    @GetMapping("/")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = getAllUsersUseCase.getAllUsers();
        
        List<UserResponseDTO> userDTOs = users.stream()
                .map(UserDTOMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Endpoint for retrieving a user by CPF.
     *
     * @param cpf The CPF of the user to retrieve
     * @return ResponseEntity with the user information
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UserResponseDTO> getUserByCpf(@PathVariable String cpf) {
        try {
            User user = getUserByCpfUseCase.getUserByCpf(cpf);
            UserResponseDTO userDTO = UserDTOMapper.toResponseDTO(user);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for deleting a user.
     *
     * @param id The ID of the user to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            deleteUserUseCase.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
