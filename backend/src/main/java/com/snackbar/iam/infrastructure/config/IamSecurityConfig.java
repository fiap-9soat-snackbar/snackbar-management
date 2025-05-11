package com.snackbar.iam.infrastructure.config;

import com.snackbar.iam.infrastructure.security.IamJwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Web security configuration for the IAM module.
 * 
 * This configuration class is responsible for setting up web security aspects of the IAM module:
 * 
 * - Configuring HTTP security settings
 * - Setting up security filters
 * - Defining URL-based access rules
 * - Configuring CSRF, CORS, and session management
 * 
 * This class focuses specifically on web security configuration and works in conjunction with
 * IamAuthenticationConfig (which handles authentication mechanisms) and IamConfig (which
 * handles business component configuration).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class IamSecurityConfig {

    private final IamJwtAuthenticationFilter iamJwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public IamSecurityConfig(
            IamJwtAuthenticationFilter iamJwtAuthenticationFilter,
            UserDetailsService userDetailsService) {
        this.iamJwtAuthenticationFilter = iamJwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean(name = "iamSecurityFilterChain")
    @Order(1)  // Higher precedence than the default filter chain
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/api/user/auth/**"),
                    new AntPathRequestMatcher("/v3/api-docs/**"),
                    new AntPathRequestMatcher("/swagger-ui/**"),
                    new AntPathRequestMatcher("/swagger-ui.html"),
                    new AntPathRequestMatcher("/actuator/health/**"),
                    new AntPathRequestMatcher("/actuator/info")
                ).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .userDetailsService(userDetailsService)
            .addFilterBefore(iamJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
