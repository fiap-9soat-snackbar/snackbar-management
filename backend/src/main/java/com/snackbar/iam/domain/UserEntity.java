package com.snackbar.iam.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Data
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    private String id;
    
    private String name;

    private String email;

    private String cpf;

    private IamRole role;

    private String password;

}
