package com.snackbar.iam.application;

import com.snackbar.cooking.domain.exceptions.UserNotFoundException;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.infrastructure.IamRepository;
import com.snackbar.iam.web.dto.LoginUserDto;
import com.snackbar.iam.web.dto.RegisterUserDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final IamRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            IamRepository userRepository,
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
        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para o CPF: " + cpf));
    }
}
