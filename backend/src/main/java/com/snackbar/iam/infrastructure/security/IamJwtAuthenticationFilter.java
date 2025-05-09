package com.snackbar.iam.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.iam.infrastructure.controllers.dto.IamErrorResponseDTO;
import com.snackbar.iam.infrastructure.security.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that validates JWT tokens and sets up Spring Security authentication.
 * This is part of the infrastructure layer in the clean architecture.
 * Named with "Iam" prefix to avoid naming conflicts with legacy components.
 */
@Component
public class IamJwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(IamJwtAuthenticationFilter.class);
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public IamJwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            ObjectMapper objectMapper
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // If no Authorization header or not a Bearer token, continue with the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String userCpf = null;
        
        // Extract username (CPF) from token without throwing exceptions
        try {
            userCpf = jwtService.extractUsername(jwt);
            logger.debug("Extracted CPF from token: {}", userCpf);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired");
            handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.EXPIRED_TOKEN, 
                    "JWT token expired");
            return;
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature");
            handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.INVALID_SIGNATURE, 
                    "Invalid JWT signature");
            return;
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token");
            handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.MALFORMED_TOKEN, 
                    "Malformed JWT token");
            return;
        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.OTHER, 
                    "JWT token validation failed");
            return;
        } catch (Exception e) {
            logger.error("Error processing JWT token", e);
            handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.OTHER, 
                    "Error processing JWT token");
            return;
        }

        // If we couldn't extract a username, continue with the filter chain
        if (userCpf == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userCpf);
                logger.debug("Loaded user details for CPF: {}", userCpf);

                // Validate token without throwing exceptions
                boolean isTokenValid = false;
                try {
                    isTokenValid = jwtService.isTokenValid(jwt, userDetails);
                } catch (Exception e) {
                    logger.warn("Token validation failed: {}", e.getMessage());
                    handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.INVALID_SIGNATURE, 
                            "Token validation failed");
                    return;
                }

                if (isTokenValid) {
                    logger.debug("Token is valid for user: {}", userCpf);
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.debug("Authentication set in SecurityContextHolder");
                } else {
                    logger.warn("Token validation failed for user: {}", userCpf);
                    handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.INVALID_SIGNATURE, 
                            "Token validation failed");
                    return;
                }
            } catch (UsernameNotFoundException e) {
                // User no longer exists in the database, but token might still be valid
                logger.warn("User from token no longer exists: {}", userCpf);
                handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.USER_NOT_FOUND, 
                        "User not found: " + userCpf);
                return;
            } catch (Exception e) {
                logger.error("Error during authentication", e);
                handleJwtValidationFailure(response, JwtAuthenticationException.JwtErrorType.OTHER, 
                        "Authentication error");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * Handles JWT validation failures by sending an appropriate error response.
     *
     * @param response The HTTP response
     * @param errorType The type of JWT error
     * @param logMessage The message to log (not sent to client)
     * @throws IOException If an I/O error occurs
     */
    private void handleJwtValidationFailure(
            HttpServletResponse response, 
            JwtAuthenticationException.JwtErrorType errorType,
            String logMessage
    ) throws IOException {
        // Determine appropriate status code and client message based on error type
        HttpStatus status;
        String clientMessage;
        
        switch (errorType) {
            case EXPIRED_TOKEN:
                status = HttpStatus.UNAUTHORIZED;
                clientMessage = "Authentication token has expired";
                break;
            case INVALID_SIGNATURE:
                status = HttpStatus.UNAUTHORIZED;
                clientMessage = "Invalid authentication token";
                break;
            case MALFORMED_TOKEN:
                status = HttpStatus.BAD_REQUEST;
                clientMessage = "Malformed authentication token";
                break;
            case USER_NOT_FOUND:
                status = HttpStatus.UNAUTHORIZED;
                clientMessage = "Authentication failed";
                break;
            default:
                status = HttpStatus.UNAUTHORIZED;
                clientMessage = "Authentication failed";
        }
        
        // Log the detailed message for debugging
        logger.warn("{}: {}", clientMessage, logMessage);
        
        // Clear any existing authentication
        SecurityContextHolder.clearContext();
        
        // Set response status and content type
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        // Write the error response as JSON
        IamErrorResponseDTO errorResponse = IamErrorResponseDTO.error(clientMessage);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
