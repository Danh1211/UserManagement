package com.example.usermanagement.controller;

import com.example.usermanagement.config.UserContext;
import com.example.usermanagement.dtos.request.LoginRequest;
import com.example.usermanagement.dtos.response.LoginResponse;
import com.example.usermanagement.dtos.response.UserResponse;
import com.example.usermanagement.security.UserSessionService;
import com.example.usermanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserSessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        String username = UserContext.get().getUsername();
        sessionService.revoke(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUser(@RequestParam String username){
        return ResponseEntity.ok(authService.getUser(username));
    }
}
