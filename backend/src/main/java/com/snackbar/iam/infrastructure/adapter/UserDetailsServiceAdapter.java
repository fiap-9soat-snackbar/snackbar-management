package com.snackbar.iam.infrastructure.adapter;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.infrastructure.security.UserDetailsAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Adapter that bridges between our domain User entity and Spring Security's UserDetailsService.
 * This is a temporary component to maintain compatibility during the transition to clean architecture.
 * Marked as @Primary to be preferred over the legacy UserDetailsService.
 * Explicitly named with @Qualifier to ensure consistent reference across the application.
 */
@Service("userDetailsServiceAdapter")
@Primary
public class UserDetailsServiceAdapter implements UserDetailsService {

    private final UserGateway userGateway;

    public UserDetailsServiceAdapter(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        return userGateway.findByCpf(cpf)
                .map(UserDetailsAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with CPF: " + cpf));
    }
}
