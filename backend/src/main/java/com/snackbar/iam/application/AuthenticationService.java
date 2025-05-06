package com.snackbar.iam.application;

import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.infrastructure.controllers.dto.LoginRequestDTO;
import com.snackbar.iam.infrastructure.controllers.dto.RegisterUserRequestDTO;
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
@Service("legacyAuthenticationService")
@Deprecated
public class AuthenticationService {
    protected final IamRepository userRepository;
    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationManager authenticationManager;

    public AuthenticationService(
            @Qualifier("iamRepositoryAdapter") IamRepository userRepository,
            @Qualifier("legacyAuthenticationManager") AuthenticationManager authenticationManager,
            @Qualifier("legacyPasswordEncoder") PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity signup(RegisterUserRequestDTO input) {
        UserEntity user = UserEntity.builder()
                .name(input.fullName())
                .email(input.email())
                .role(input.role())
                .cpf(input.cpf())
                .password(passwordEncoder.encode(input.password()))
                .build();

        return userRepository.save(user);
    }

    public UserDetailsEntity authenticate(LoginRequestDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.cpf(),
                        input.password()
                )
        );

        return findByCpf(input.cpf());
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
