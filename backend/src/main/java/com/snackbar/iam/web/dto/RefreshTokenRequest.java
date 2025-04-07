package com.snackbar.iam.web.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RefreshTokenRequest {

    private String cpf;
}
