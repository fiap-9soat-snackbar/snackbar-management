package com.snackbar.iam.application;

import com.snackbar.iam.infrastructure.config.IamSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Adapter for SecurityConfiguration that delegates to IamSecurityConfig.
 * This class exists for backward compatibility and will be removed in future versions.
 * 
 * @deprecated Use {@link com.snackbar.iam.infrastructure.config.IamSecurityConfig} instead
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurationAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfigurationAdapter.class);
    
    private final IamSecurityConfig iamSecurityConfig;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfigurationAdapter(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            IamSecurityConfig iamSecurityConfig
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.iamSecurityConfig = iamSecurityConfig;
        logger.info("SecurityConfigurationAdapter initialized as adapter to IamSecurityConfig");
    }

    /**
     * Primary security filter chain that handles non-v2 API requests
     */
    @Bean
    @Primary
    @Order(2) // Lower priority than IamSecurityConfig's filter chain
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // This will handle all non-v2 API requests
        http.securityMatcher(request -> !request.getRequestURI().startsWith("/api/v2/"))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/user/auth/**").permitAll()
                        .requestMatchers("/api/checkout").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Primary
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET","POST"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
