package com.snackbar.iam.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication configuration for the IAM module.
 * 
 * This configuration class is responsible for setting up authentication mechanisms:
 * 
 * - Configuring authentication providers
 * - Setting up authentication managers
 * - Defining password encoders
 * - Connecting UserDetailsService to the authentication system
 * 
 * This class focuses specifically on authentication mechanisms and works in conjunction with
 * IamSecurityConfig (which handles web security) and IamConfig (which handles business
 * component configuration).
 */
@Configuration
public class IamAuthenticationConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public IamAuthenticationConfig(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean(name = "iamAuthenticationProvider")
    @Primary
    public AuthenticationProvider iamAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean(name = "iamAuthenticationManager")
    @Primary
    public AuthenticationManager iamAuthenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
