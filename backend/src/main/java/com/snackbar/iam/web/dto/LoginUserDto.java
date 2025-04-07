package com.snackbar.iam.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginUserDto {

    private String cpf;

    private String password;

    private Boolean anonymous = false;
}
