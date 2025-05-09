package com.snackbar.iam.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication configuration for the IAM module.
 * 
 * This configuration class is responsible for setting up authentication mechanisms:
 * 
 * - Setting up authentication managers
 * - Defining password encoders
 * 
 * This class focuses specifically on authentication mechanisms and works in conjunction with
 * IamSecurityConfig (which handles web security) and IamConfig (which handles business
 * component configuration).
 */
@Configuration
public class IamAuthenticationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
