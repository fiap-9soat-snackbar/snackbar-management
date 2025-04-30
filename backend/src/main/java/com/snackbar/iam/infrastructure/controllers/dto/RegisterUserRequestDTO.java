package com.snackbar.iam.infrastructure.controllers.dto;

import com.snackbar.iam.domain.IamRole;

/**
 * DTO for user registration requests.
 * Implemented as a record for immutability and consistency with the Product module.
 */
public record RegisterUserRequestDTO(
    String fullName,
    String email,
    String cpf,
    String password,
    IamRole role
) {
}
