package com.snackbar.iam.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterUserDto {
    private String email;

    private String password;

    private String cpf;

    private String role;

    private String fullName;
}
