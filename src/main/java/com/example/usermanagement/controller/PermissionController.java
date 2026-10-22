package com.example.usermanagement.controller;

import com.example.usermanagement.dtos.common.ApiRequest;
import com.example.usermanagement.dtos.common.ApiResponse;
import com.example.usermanagement.dtos.request.PermissionRequest;
import com.example.usermanagement.dtos.response.PermissionResponse;
import com.example.usermanagement.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> list(){
        return ResponseEntity.ok(permissionService.getList());
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermission(@RequestParam String permissionCode){
        return ResponseEntity.ok(permissionService.getByPermissionCode(permissionCode));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@RequestBody ApiRequest<PermissionRequest> request){
        return ResponseEntity.ok(permissionService.createPermission(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(@RequestBody ApiRequest<PermissionRequest> request){
        return ResponseEntity.ok(permissionService.updatePermission(request));
    }
}
