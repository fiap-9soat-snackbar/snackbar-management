package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get All Users Use Case Tests")
class GetAllUsersUseCaseTest {

    @Mock
    private UserGateway userGateway;

    private GetAllUsersUseCase getAllUsersUseCase;

    @BeforeEach
    void setUp() {
        getAllUsersUseCase = new GetAllUsersUseCase(userGateway);
    }

    @Nested
    @DisplayName("When users exist")
    class WhenUsersExist {
        
        @Test
        @DisplayName("Should return all users when they exist in the system")
        void shouldReturnAllUsersWhenUsersExist() {
            // Given: There are users in the system
            User user1 = mock(User.class);
            User user2 = mock(User.class);
            List<User> expectedUsers = Arrays.asList(user1, user2);
            
            when(userGateway.findAll()).thenReturn(expectedUsers);
            
            // When: Getting all users
            List<User> actualUsers = getAllUsersUseCase.getAllUsers();
            
            // Then: All users should be returned
            assertEquals(expectedUsers.size(), actualUsers.size());
            assertEquals(expectedUsers, actualUsers);
            verify(userGateway, times(1)).findAll();
        }
        
        @Test
        @DisplayName("Should return users with correct properties")
        void shouldReturnUsersWithCorrectProperties() {
            // Given: There are users with specific properties in the system
            // Using valid CPF numbers that pass the verification algorithm
            User user1 = new User("1", "John Doe", "john@example.com", "52998224725", IamRole.ADMIN, "password");
            User user2 = new User("2", "Jane Smith", "jane@example.com", "40532176871", IamRole.CONSUMER, "password");
            List<User> expectedUsers = Arrays.asList(user1, user2);
            
            when(userGateway.findAll()).thenReturn(expectedUsers);
            
            // When: Getting all users
            List<User> actualUsers = getAllUsersUseCase.getAllUsers();
            
            // Then: Users should have the correct properties
            assertEquals(2, actualUsers.size());
            
            User firstUser = actualUsers.get(0);
            assertEquals("1", firstUser.getId());
            assertEquals("John Doe", firstUser.getName());
            assertEquals("john@example.com", firstUser.getEmail());
            assertEquals("52998224725", firstUser.getCpf());
            assertEquals(IamRole.ADMIN, firstUser.getRole());
            
            User secondUser = actualUsers.get(1);
            assertEquals("2", secondUser.getId());
            assertEquals("Jane Smith", secondUser.getName());
            assertEquals("jane@example.com", secondUser.getEmail());
            assertEquals("40532176871", secondUser.getCpf());
            assertEquals(IamRole.CONSUMER, secondUser.getRole());
        }
    }

    @Nested
    @DisplayName("When no users exist")
    class WhenNoUsersExist {
        
        @Test
        @DisplayName("Should return empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsersExist() {
            // Given: There are no users in the system
            when(userGateway.findAll()).thenReturn(Collections.emptyList());
            
            // When: Getting all users
            List<User> users = getAllUsersUseCase.getAllUsers();
            
            // Then: An empty list should be returned
            assertTrue(users.isEmpty());
            verify(userGateway, times(1)).findAll();
        }
    }
    
    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {
        
        @Test
        @DisplayName("Should handle null return from gateway")
        void shouldHandleNullReturnFromGateway() {
            // Given: The gateway returns null (which shouldn't happen in practice but we test for robustness)
            when(userGateway.findAll()).thenReturn(null);
            
            // When: Getting all users
            List<User> users = getAllUsersUseCase.getAllUsers();
            
            // Then: An empty list should be returned instead of null
            assertTrue(users.isEmpty());
            verify(userGateway, times(1)).findAll();
        }
    }
}
