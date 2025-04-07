package com.snackbar.iam.web.dto;

import com.snackbar.iam.domain.IamRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String cpf;
    private IamRole role;

}
