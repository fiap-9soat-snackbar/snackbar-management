package com.snackbar.iam.domain.event;

import com.snackbar.iam.domain.entity.User;

/**
 * Domain event that is triggered when a new user is created.
 * Contains relevant information about the created user.
 */
public class UserCreatedEvent extends UserDomainEvent {
    private final String userCpf;
    private final String userEmail;
    private final String userName;

    public UserCreatedEvent(User user) {
        super(user.getId(), "USER_CREATED");
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
