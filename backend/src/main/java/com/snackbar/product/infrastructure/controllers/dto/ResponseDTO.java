package com.snackbar.product.infrastructure.controllers.dto;

public record ResponseDTO(boolean success, String message, Object data) {
}
