package com.snackbar.iam.infrastructure.controllers.dto;

import com.snackbar.iam.domain.IamRole;

/**
 * DTO for user responses.
 * Implemented as a record for immutability and consistency with the Product module.
 */
public record UserResponseDTO(
    String id,
    String name,
    String email,
    String cpf,
    IamRole role
) {
}
