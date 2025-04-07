package com.snackbar.iam.application;

import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.infrastructure.IamRepository;
import org.springframework.stereotype.Component;

@Component
public class IamServiceImpl implements IamService {

    private final IamRepository iamRepository;

    public IamServiceImpl(IamRepository iamRepository) {
        this.iamRepository = iamRepository;
    }

    @Override
    public UserEntity create(UserEntity entity) {
        this.iamRepository.save(entity);
        return entity;
    }
}
