package com.snackbar.iam.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;

    private long expiresIn;

    public LoginResponse(String jwtToken, long expirationTime) {
        this.token = jwtToken;
        this.expiresIn = expirationTime;
    }
}
