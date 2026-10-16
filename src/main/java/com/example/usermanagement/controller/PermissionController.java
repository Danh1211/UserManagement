package com.example.usermanagement.controller;

import com.example.usermanagement.dtos.request.PermissionRequest;
import com.example.usermanagement.dtos.request.RoleRequest;
import com.example.usermanagement.dtos.response.PermissionResponse;
import com.example.usermanagement.dtos.response.RoleResponse;
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

//    public PermissionController(PermissionService permissionService) {
//        this.permissionService = permissionService;
//    }

    @GetMapping("/list")
    public ResponseEntity<List<PermissionResponse>> list(){
        return ResponseEntity.ok(permissionService.getList());
    }

    @GetMapping("/detail")
    public ResponseEntity<PermissionResponse> getPermission(@RequestParam String permissionCode){
        return ResponseEntity.ok(permissionService.getByPermissionCode(permissionCode));
    }

    @PostMapping("/create")
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request){
        return ResponseEntity.ok(permissionService.createPermission(request));
    }

    @PutMapping("/update")
    public ResponseEntity<PermissionResponse> update(@RequestBody PermissionRequest request){
        return ResponseEntity.ok(permissionService.updatePermission(request));
    }
}
