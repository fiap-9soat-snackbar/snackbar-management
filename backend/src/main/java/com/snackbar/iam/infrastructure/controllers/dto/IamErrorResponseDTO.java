package com.snackbar.iam.infrastructure.controllers.dto;

/**
 * DTO for IAM error responses.
 * Named with "Iam" prefix to avoid naming conflicts with other modules.
 * Implemented as a record for immutability and consistency with other DTOs.
 */
public record IamErrorResponseDTO(
    boolean success,
    String message,
    Object data
) {
    /**
     * Constructor for error responses without additional data.
     *
     * @param message The error message
     * @return An error response DTO with the given message and no data
     */
    public static IamErrorResponseDTO error(String message) {
        return new IamErrorResponseDTO(false, message, null);
    }
    
    /**
     * Constructor for error responses with additional data.
     *
     * @param message The error message
     * @param data Additional error data
     * @return An error response DTO with the given message and data
     */
    public static IamErrorResponseDTO error(String message, Object data) {
        return new IamErrorResponseDTO(false, message, data);
    }
}
