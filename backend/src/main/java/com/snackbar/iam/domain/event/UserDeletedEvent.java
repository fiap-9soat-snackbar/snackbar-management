package com.snackbar.iam.domain.event;

/**
 * Domain event that is triggered when a user is deleted.
 * Contains the ID of the deleted user.
 */
public class UserDeletedEvent extends UserDomainEvent {
    private final String userCpf;

    public UserDeletedEvent(String userId, String userCpf) {
        super(userId, "USER_DELETED");
        this.userCpf = userCpf;
    }

    public String getUserCpf() {
        return userCpf;
    }
}
