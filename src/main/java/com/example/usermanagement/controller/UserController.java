package com.example.usermanagement.controller;

import com.example.usermanagement.dtos.common.ApiRequest;
import com.example.usermanagement.dtos.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<UserResponse>>> list() { return ResponseEntity.ok(userService.list()); }

    @PutMapping("/toggle")
    public ResponseEntity<ApiResponse<UserResponse>> toggleRole(@RequestBody ApiRequest<ToggleRoleRequest> request){
        return ResponseEntity.ok(userService.toggleRole(request));
    }
}
