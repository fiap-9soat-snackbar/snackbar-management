package com.snackbar.iam.application.usecases;

import com.snackbar.iam.application.gateways.UserGateway;
import com.snackbar.iam.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
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
class GetAllUsersUseCaseTest {

    @Mock
    private UserGateway userGateway;

    private GetAllUsersUseCase getAllUsersUseCase;

    @BeforeEach
    void setUp() {
        getAllUsersUseCase = new GetAllUsersUseCase(userGateway);
    }

    @Test
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
