package com.snackbar.iam.domain.event;

import com.snackbar.iam.domain.entity.User;

/**
 * Domain event that is triggered when a user is updated.
 * Contains relevant information about the updated user.
 */
public class UserUpdatedEvent extends UserDomainEvent {
    private final String userCpf;
    private final String userEmail;
    private final String userName;

    public UserUpdatedEvent(User user) {
        super(user.getId(), "USER_UPDATED");
        this.userCpf = user.getCpf();
        this.userEmail = user.getEmail();
        this.userName = user.getName();
    }

    public String getUserCpf() {
        return userCpf;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }
}
