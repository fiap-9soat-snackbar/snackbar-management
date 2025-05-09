package com.snackbar.iam.infrastructure.security;

import com.snackbar.iam.application.gateways.UserGateway;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security's UserDetailsService that uses our domain UserGateway.
 * This service loads user-specific data for authentication purposes.
 */
@Service
public class IamUserDetailsService implements UserDetailsService {

    private final UserGateway userGateway;

    public IamUserDetailsService(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        return userGateway.findByCpf(cpf)
                .map(UserDetailsAdapter::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with CPF: " + cpf));
    }
}
