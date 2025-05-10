package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import com.snackbar.iam.domain.exceptions.InvalidCredentialsException;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authenticate User Use Case Tests")
class AuthenticateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthenticateUserUseCase authenticateUserUseCase;

    @BeforeEach
    void setUp() {
        authenticateUserUseCase = new AuthenticateUserUseCase(userGateway, passwordEncoder);
    }

    @Nested
    @DisplayName("When authenticating with valid credentials")
    class WhenAuthenticatingWithValidCredentials {
        
        @Test
        @DisplayName("Should authenticate user successfully with correct credentials")
        void shouldAuthenticateUserWithValidCredentials() {
            // Given: A user exists and the password is correct
            String cpf = "52998224725";
            String password = "password123";
            String encodedPassword = "encodedPassword123";
            
            User user = mock(User.class);
            when(user.getPassword()).thenReturn(encodedPassword);
            
            when(userGateway.findByCpf(cpf)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
            
            // When: Authenticating with valid credentials
            User authenticatedUser = authenticateUserUseCase.authenticate(cpf, password);
            
            // Then: The user should be authenticated
            assertEquals(user, authenticatedUser);
            verify(userGateway).findByCpf(cpf);
            verify(passwordEncoder).matches(password, encodedPassword);
        }
    }

    @Nested
    @DisplayName("When user does not exist")
    class WhenUserDoesNotExist {
        
        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserDoesNotExist() {
            // Given: No user exists with the specified CPF
            String cpf = "52998224725";
            String password = "password123";
            
            when(userGateway.findByCpf(cpf)).thenReturn(Optional.empty());
            
            // When/Then: Authenticating a non-existent user should throw an exception
            UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> authenticateUserUseCase.authenticate(cpf, password)
            );
            
            // Then: The exception message should contain the CPF
            assertTrue(exception.getMessage().contains(cpf));
            verify(userGateway).findByCpf(cpf);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("When password is incorrect")
    class WhenPasswordIsIncorrect {
        
        @Test
        @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
        void shouldThrowExceptionWhenPasswordIsIncorrect() {
            // Given: A user exists but the password is incorrect
            String cpf = "52998224725";
            String password = "password123";
            String encodedPassword = "encodedPassword123";
            
            User user = mock(User.class);
            when(user.getPassword()).thenReturn(encodedPassword);
            
            when(userGateway.findByCpf(cpf)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);
            
            // When/Then: Authenticating with incorrect password should throw an exception
            InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserUseCase.authenticate(cpf, password)
            );
            
            // Then: The exception message should contain the CPF
            assertTrue(exception.getMessage().contains(cpf));
            verify(userGateway).findByCpf(cpf);
            verify(passwordEncoder).matches(password, encodedPassword);
        }
    }

    @Nested
    @DisplayName("When authenticating anonymously")
    class WhenAuthenticatingAnonymously {
        
        @Test
        @DisplayName("Should create anonymous user with correct properties")
        void shouldCreateAnonymousUser() {
            // Given: The need for an anonymous user
            
            // When: Creating an anonymous user
            User anonymousUser = authenticateUserUseCase.authenticateAnonymous();
            
            // Then: An anonymous user should be created with the correct properties
            assertNotNull(anonymousUser);
            assertEquals("anonymous", anonymousUser.getId());
            assertEquals("Anonymous User", anonymousUser.getName());
            assertEquals(IamRole.CONSUMER, anonymousUser.getRole());
            
            // No interactions with gateway or password encoder
            verifyNoInteractions(userGateway);
            verifyNoInteractions(passwordEncoder);
        }
        
        @Test
        @DisplayName("Should create a new anonymous user instance each time")
        void shouldCreateNewAnonymousUserInstanceEachTime() {
            // Given: Multiple requests for anonymous users
            
            // When: Creating multiple anonymous users
            User anonymousUser1 = authenticateUserUseCase.authenticateAnonymous();
            User anonymousUser2 = authenticateUserUseCase.authenticateAnonymous();
            
            // Then: Each call should return a new instance
            assertNotSame(anonymousUser1, anonymousUser2);
            
            // But with the same properties
            assertEquals(anonymousUser1.getId(), anonymousUser2.getId());
            assertEquals(anonymousUser1.getName(), anonymousUser2.getName());
            assertEquals(anonymousUser1.getRole(), anonymousUser2.getRole());
        }
    }
}
