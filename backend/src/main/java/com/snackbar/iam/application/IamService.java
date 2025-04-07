package com.snackbar.iam.application;

import com.snackbar.iam.domain.UserEntity;

public interface IamService {

    public UserEntity create(UserEntity entity);

}
