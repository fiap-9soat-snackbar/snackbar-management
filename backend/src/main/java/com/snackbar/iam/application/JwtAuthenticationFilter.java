package com.snackbar.iam.application;

import com.snackbar.iam.infrastructure.security.IamJwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Legacy JWT authentication filter that delegates to the new IamJwtAuthenticationFilter.
 * This class exists for backward compatibility and will be removed in future versions.
 * 
 * @deprecated Use {@link com.snackbar.iam.infrastructure.security.IamJwtAuthenticationFilter} instead
 */
@Component
@Deprecated
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final IamJwtAuthenticationFilter delegate;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Constructor that accepts the legacy dependencies but delegates to the new implementation.
     */
    public JwtAuthenticationFilter(
            @Qualifier("legacyJwtService") JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver,
            IamJwtAuthenticationFilter delegate
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.delegate = delegate;
        logger.info("Legacy JwtAuthenticationFilter initialized as adapter to IamJwtAuthenticationFilter");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Simply delegate to the new implementation
        delegate.doFilter(request, response, filterChain);
    }
}
