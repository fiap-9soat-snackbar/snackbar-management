package com.snackbar.iam.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.iam.infrastructure.controllers.dto.IamErrorResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamJwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private ServletOutputStream outputStream;

    private IamJwtAuthenticationFilter filter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String VALID_CPF = "12345678900";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        filter = new IamJwtAuthenticationFilter(jwtService, userDetailsService, objectMapper);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_noAuthHeader_shouldContinueFilterChain() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_nonBearerToken_shouldContinueFilterChain() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("Basic abc123");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_validToken_shouldSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(VALID_CPF);
        when(userDetailsService.loadUserByUsername(VALID_CPF)).thenReturn(userDetails);
        when(jwtService.isTokenValid(VALID_TOKEN, userDetails)).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_expiredToken_shouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(eq(outputStream), any(IamErrorResponseDTO.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidSignature_shouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenThrow(new SignatureException("Invalid signature"));
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(eq(outputStream), any(IamErrorResponseDTO.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_malformedToken_shouldReturnBadRequest() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenThrow(new MalformedJwtException("Malformed token"));
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(eq(outputStream), any(IamErrorResponseDTO.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_otherJwtException_shouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenThrow(new JwtException("Other JWT error"));
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(eq(outputStream), any(IamErrorResponseDTO.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_userNotFound_shouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(VALID_CPF);
        when(userDetailsService.loadUserByUsername(VALID_CPF)).thenThrow(new UsernameNotFoundException("User not found"));
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(eq(outputStream), any(IamErrorResponseDTO.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_shouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(VALID_CPF);
        when(userDetailsService.loadUserByUsername(VALID_CPF)).thenReturn(userDetails);
        when(jwtService.isTokenValid(VALID_TOKEN, userDetails)).thenReturn(false);
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(eq(outputStream), any(IamErrorResponseDTO.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenValidationException_shouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(VALID_CPF);
        when(userDetailsService.loadUserByUsername(VALID_CPF)).thenReturn(userDetails);
        when(jwtService.isTokenValid(VALID_TOKEN, userDetails)).thenThrow(new RuntimeException("Token validation error"));
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(objectMapper).writeValue(eq(outputStream), any(IamErrorResponseDTO.class));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_existingAuthentication_shouldSkipAuthentication() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(VALID_CPF);
        
        // Mock existing authentication
        Authentication existingAuth = mock(Authentication.class);
        when(existingAuth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(existingAuth);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_nullUsername_shouldContinueFilterChain() throws ServletException, IOException {
        // Given
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + VALID_TOKEN);
        when(jwtService.extractUsername(VALID_TOKEN)).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}
