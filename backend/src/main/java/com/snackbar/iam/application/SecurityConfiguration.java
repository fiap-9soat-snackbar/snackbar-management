package com.snackbar.iam.application;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * Legacy security configuration class.
 * 
 * @deprecated Use {@link com.snackbar.iam.infrastructure.config.IamSecurityConfig} instead.
 * This class is maintained for backward compatibility and will be removed in future versions.
 */
@Configuration("legacySecurityConfiguration")
@EnableWebSecurity
@Deprecated
public class SecurityConfiguration {
    protected final AuthenticationProvider authenticationProvider;
    protected final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            @Qualifier("legacyJwtAuthenticationFilter") JwtAuthenticationFilter jwtAuthenticationFilter,
            @Qualifier("legacyAuthenticationProvider") AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean("legacySecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(new AntPathRequestMatcher("/legacy-path-not-used/**"))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/swagger-ui/**").permitAll()
                        .requestMatchers( "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/user/auth/**").permitAll()
                        .requestMatchers("/api/checkout").permitAll()
                        .requestMatchers( "/actuator/health/**").permitAll()
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


    @Bean("legacyCorsConfigurationSource")
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
