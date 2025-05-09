package com.snackbar.iam.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Legacy user entity used for MongoDB persistence.
 * 
 * @deprecated Use {@link com.snackbar.iam.domain.entity.User} instead.
 * This class is maintained for backward compatibility and will be removed in future versions.
 */
@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Deprecated
public class UserEntity {
    @Id
    private String id;
    
    private String name;

    private String email;

    private String cpf;

    private IamRole role;

    private String password;

}
