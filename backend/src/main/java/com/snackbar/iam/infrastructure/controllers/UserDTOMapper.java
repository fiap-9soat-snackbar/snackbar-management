package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.UpdateUserRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.UserResponseDTO;

/**
 * Mapper class to convert between domain User and DTOs.
 */
public class UserDTOMapper {
    
    /**
     * Maps a RegisterUserRequestDTO to a domain User.
     *
     * @param dto The DTO to map
     * @return The mapped domain User
     */
    public static User toUser(RegisterUserRequestDTO dto) {
        return new User(
                null, // ID will be generated
                dto.fullName(),
                dto.email(),
                dto.cpf(),
                dto.role(),
                dto.password()
        );
    }
    
    /**
     * Maps an UpdateUserRequestDTO to a domain User.
     * Following PUT semantics, all fields from the DTO are mapped to the domain entity.
     *
     * @param dto The DTO to map
     * @return The mapped domain User
     */
    public static User toUser(UpdateUserRequestDTO dto) {
        return new User(
                null, // ID will be set by the use case
                dto.getName(),
                dto.getEmail(),
                dto.getCpf(),
                IamRole.valueOf(dto.getRole()), // Convert String to IamRole enum
                dto.getPassword()
        );
    }
    
    /**
     * Maps a domain User to a UserResponseDTO.
     *
     * @param user The domain User to map
     * @return The mapped UserResponseDTO
     */
    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getRole()
        );
    }
}
