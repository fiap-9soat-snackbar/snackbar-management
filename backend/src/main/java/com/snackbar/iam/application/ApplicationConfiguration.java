package com.snackbar.iam.application;

import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.infrastructure.adapter.IamRepositoryAdapter;
import com.snackbar.iam.infrastructure.security.UserDetailsAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Legacy application configuration class.
 * 
 * @deprecated This class is maintained for backward compatibility and will be removed in future versions.
 */
@Configuration
@EnableWebSecurity
@Deprecated
public class ApplicationConfiguration {
    private final IamRepositoryAdapter userRepository;

    public ApplicationConfiguration(@Qualifier("iamRepositoryAdapter") IamRepositoryAdapter userRepository) {
        this.userRepository = userRepository;
    }

    @Bean("legacyUserDetailsService")
    UserDetailsService userDetailsService() {
        return cpf -> {
            UserEntity userEntity = userRepository.findByCpf(cpf)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with CPF: " + cpf));
            
            // Convert UserEntity to User domain entity
            User user = new User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getCpf(),
                userEntity.getRole(),
                userEntity.getPassword()
            );
            
            // Use UserDetailsAdapter instead of UserDetailsEntity
            return new UserDetailsAdapter(user);
        };
    }

    @Bean("legacyPasswordEncoder")
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("legacyAuthenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean("legacyAuthenticationProvider")
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
