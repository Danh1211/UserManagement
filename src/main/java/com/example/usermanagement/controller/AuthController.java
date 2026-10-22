package com.example.usermanagement.controller;

import com.example.usermanagement.config.UserContext;
import com.example.usermanagement.config.UserSessionInfo;
import com.example.usermanagement.dtos.common.ApiRequest;
import com.example.usermanagement.dtos.common.ApiResponse;
import com.example.usermanagement.dtos.request.LoginRequest;
import com.example.usermanagement.dtos.response.LoginResponse;
import com.example.usermanagement.dtos.response.UserResponse;
import com.example.usermanagement.exception.NoAuthenticationError;
import com.example.usermanagement.security.JwtProperties;
import com.example.usermanagement.security.UserSessionService;
import com.example.usermanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserSessionService sessionService;
    private final JwtProperties jwtProperties;

    @Value("${app.cookie.secure:false}")
    private boolean secure;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody ApiRequest<LoginRequest> request) {
        ApiResponse<LoginResponse> response = authService.login(request);
        String token = response.getData().getAccessToken();

        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(Duration.ofSeconds(jwtProperties.getTokenValidityInSeconds()))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        if (UserContext.get() != null) {
            String username = UserContext.get().getUsername();
            sessionService.revoke(username);
        }
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSessionInfo>> getMe() {
        UserSessionInfo userSession = UserContext.get();
        if (userSession == null) {
            throw new NoAuthenticationError("Chưa đăng nhập hoặc token không hợp lệ!");
        }
        return ResponseEntity.ok(ApiResponse.ok(userSession));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@RequestParam String username) {
        return ResponseEntity.ok(authService.getUser(username));
    }
}
