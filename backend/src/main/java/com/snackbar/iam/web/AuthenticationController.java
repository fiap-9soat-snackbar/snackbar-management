package com.snackbar.iam.web;

import com.snackbar.iam.application.AuthenticationService;
import com.snackbar.iam.application.JwtService;
import com.snackbar.iam.application.UserService;
import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.web.dto.LoginResponse;
import com.snackbar.iam.web.dto.LoginUserDto;
import com.snackbar.iam.web.dto.RegisterUserDto;
import com.snackbar.iam.web.dto.RefreshTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            UserService userService
    ) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterUserDto registerUserDto) {
        UserEntity registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        String jwtToken;
        UserDetailsEntity authenticatedUser;

        if (Boolean.TRUE.equals(loginUserDto.getAnonymous())) {
            authenticatedUser = UserDetailsEntity.builder()
                .cpf("anonymous")
                .role(IamRole.CONSUMER)
                .build();
            jwtToken = jwtService.generateToken(authenticatedUser);
        } else {
            authenticatedUser = authenticationService.authenticate(loginUserDto);
            jwtToken = jwtService.generateToken(authenticatedUser);
        }

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        UserDetailsEntity userDetails = authenticationService.findByCpf(refreshTokenRequest.getCpf());

        String newJwtToken = jwtService.generateToken(userDetails);

        LoginResponse loginResponse = new LoginResponse(
                newJwtToken,
                jwtService.getExpirationTime()
        );

        return ResponseEntity.ok(loginResponse);
    }

}
