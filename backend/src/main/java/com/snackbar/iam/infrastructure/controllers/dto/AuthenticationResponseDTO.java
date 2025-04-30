package com.snackbar.iam.infrastructure.controllers.dto;

/**
 * DTO for authentication responses.
 * Implemented as a record for immutability and consistency with the Product module.
 * Maintains compatibility with the existing API contract.
 */
public record AuthenticationResponseDTO(
    String token,
    long expiresIn,
    UserResponseDTO user
) {
    /**
     * Constructor without user details for backward compatibility.
     */
    public AuthenticationResponseDTO(String token, long expiresIn) {
        this(token, expiresIn, null);
    }
}
