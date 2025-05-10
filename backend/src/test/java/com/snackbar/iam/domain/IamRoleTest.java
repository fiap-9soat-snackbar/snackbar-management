package com.snackbar.iam.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IamRoleTest {

    @Test
    void shouldHaveCorrectEnumValues() {
        // Given: The IamRole enum exists in the system
        
        // When: We retrieve all enum values
        IamRole[] roles = IamRole.values();
        
        // Then: It should contain exactly the expected values in the correct order
        assertEquals(2, roles.length);
        assertArrayEquals(
            new IamRole[] {IamRole.CONSUMER, IamRole.ADMIN},
            roles
        );
    }

    @Test
    void shouldRetrieveEnumValuesByName() {
        // Given: Valid enum value names
        String consumerRoleName = "CONSUMER";
        String adminRoleName = "ADMIN";
        
        // When: We retrieve enum values by name
        IamRole consumerRole = IamRole.valueOf(consumerRoleName);
        IamRole adminRole = IamRole.valueOf(adminRoleName);
        
        // Then: The correct enum values should be returned
        assertEquals(IamRole.CONSUMER, consumerRole);
        assertEquals(IamRole.ADMIN, adminRole);
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        // Given: An invalid enum value name
        String invalidRoleName = "INVALID_ROLE";
        
        // When/Then: Attempting to retrieve an enum with an invalid name should throw an exception
        assertThrows(IllegalArgumentException.class, () -> IamRole.valueOf(invalidRoleName));
    }
}
