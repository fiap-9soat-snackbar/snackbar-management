package com.snackbar.iam.application;

import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.web.dto.LoginUserDto;
import com.snackbar.iam.web.dto.RegisterUserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Legacy authentication service.
 * 
 * @deprecated This class is maintained for backward compatibility and will be removed in future versions.
 */
@Service
@Deprecated
public class AuthenticationService {
    protected final IamRepository userRepository;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationManager authenticationManager;

    public AuthenticationService(
            @Qualifier("iamRepositoryAdapter") IamRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity signup(RegisterUserDto input) {
        UserEntity user = UserEntity.builder()
                .name(input.getFullName())
                .email(input.getEmail())
                .role(IamRole.valueOf(input.getRole()))
                .cpf(input.getCpf())
                .password(passwordEncoder.encode(input.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public UserDetailsEntity authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getCpf(),
                        input.getPassword()
                )
        );

        return findByCpf(input.getCpf());
    }

    public UserDetailsEntity findByCpf(String cpf) {
        UserEntity user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para o CPF: " + cpf));
        
        // Convert UserEntity to UserDetailsEntity
        return UserDetailsEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .role(user.getRole())
                .password(user.getPassword())
                .build();
    }
}
