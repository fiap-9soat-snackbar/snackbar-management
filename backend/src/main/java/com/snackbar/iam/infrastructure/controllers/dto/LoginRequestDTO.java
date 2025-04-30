package com.snackbar.iam.infrastructure.controllers.dto;

/**
 * DTO for user login requests.
 * Implemented as a record for immutability and consistency with the Product module.
 */
public record LoginRequestDTO(
    String cpf,
    String password,
    Boolean anonymous
) {
    /**
     * Constructor with default value for anonymous.
     */
    public LoginRequestDTO(String cpf, String password) {
        this(cpf, password, false);
    }
}
