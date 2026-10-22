package com.example.usermanagement.controller;

import com.example.usermanagement.dtos.common.ApiRequest;
import com.example.usermanagement.dtos.common.ApiResponse;
import com.example.usermanagement.dtos.request.RoleRequest;
import com.example.usermanagement.dtos.response.RoleResponse;
import com.example.usermanagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> list(){
        return ResponseEntity.ok(roleService.getListRole());
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<RoleResponse>> getRole(@RequestParam String roleCode){
        return ResponseEntity.ok(roleService.getByRoleCode(roleCode));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@RequestBody ApiRequest<RoleRequest> request){
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@RequestBody ApiRequest<RoleRequest> request){
        return ResponseEntity.ok(roleService.updateRole(request));
    }
}
