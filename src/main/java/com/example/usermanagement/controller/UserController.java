package com.example.usermanagement.controller;

import com.example.usermanagement.dtos.request.ToggleRoleRequest;
import com.example.usermanagement.dtos.response.UserResponse;
import com.example.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> list() { return ResponseEntity.ok(userService.list()); }

    @PutMapping("/toggle")
    public ResponseEntity<UserResponse> toggleRole(@RequestBody ToggleRoleRequest request){
        return ResponseEntity.ok(userService.toggleRole(request));
    }
}
