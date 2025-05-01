package com.snackbar.iam.infrastructure.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating a user.
 * Following PUT semantics, all fields are required except password which is optional.
 */
public class UpdateUserRequestDTO {
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "CPF is required")
    private String cpf;
    
    @NotNull(message = "Role is required")
    private String role;
    
    @Size(min = 8, message = "Password must be at least 8 characters if provided")
    private String password;
    
    // Default constructor for deserialization
    public UpdateUserRequestDTO() {
    }
    
    // Constructor with all fields
    public UpdateUserRequestDTO(String name, String email, String cpf, String role, String password) {
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.role = role;
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
