package com.snackbar.iam.web;

import com.snackbar.iam.application.AuthenticationService;
import com.snackbar.iam.application.JwtService;
import com.snackbar.iam.application.UserService;
import com.snackbar.iam.domain.UserDetailsEntity;
import com.snackbar.iam.domain.UserEntity;
import com.snackbar.iam.web.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/user")
@RestController
public class UserController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public UserController(JwtService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getAll() {
        var iams = this.userService.allUsers();
        return ResponseEntity.ok(iams);
    }


    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UserResponse> getByCpf(@PathVariable("cpf") String cpf) {
        var iams = this.userService.getUserByCpf(cpf);
        return ResponseEntity.ok(iams);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
