package com.example.usermanagement.service;

import com.example.usermanagement.config.HasPermission;
import com.example.usermanagement.dtos.common.ApiRequest;
import com.example.usermanagement.dtos.common.ApiResponse;
import com.example.usermanagement.dtos.request.PermissionRequest;
import com.example.usermanagement.dtos.request.RoleRequest;
import com.example.usermanagement.dtos.response.PermissionResponse;
import com.example.usermanagement.dtos.response.RoleResponse;
import com.example.usermanagement.entity.SystemPermissionEntity;
import com.example.usermanagement.entity.SystemRoleEntity;
import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.exception.AppException;
import com.example.usermanagement.repository.PermissionRepository;
import com.example.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    @HasPermission(value = {"ROLE_VIEW"})
    public ApiResponse<List<RoleResponse>> getListRole(){
        return ApiResponse.ok(roleRepository.findAll().stream().map(this::toResponse).toList());
    }

    @Transactional(readOnly = true)
    @HasPermission(value = {"ROLE_VIEW"})
    public ApiResponse<RoleResponse> getByRoleCode(String roleCode){
        SystemRoleEntity entity = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> AppException.builder().status(ErrorMessage.NOT_FOUND).build());
        return ApiResponse.ok(toResponse(entity));
    }

    @Transactional
    @HasPermission(value = {"ROLE_CREATE"})
    public ApiResponse<RoleResponse> createRole(ApiRequest<RoleRequest> request){
        RoleRequest data = request.getData();
        if(data.getRoleCode() == null || data.getRoleCode().trim().isEmpty()){
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        if (roleRepository.existsByRoleCode(data.getRoleCode())){
            throw AppException.builder().status(ErrorMessage.ROLE_CODE_EXIST).build();
        }
        SystemRoleEntity roleEntity = new SystemRoleEntity();
        roleEntity.setRoleCode(data.getRoleCode().toUpperCase());
        roleEntity.setRoleName(data.getRoleName());
        roleEntity.setDescription(data.getDescription());

        if (data.getPermissions() != null && !data.getPermissions().isEmpty()) {
            List<SystemPermissionEntity> perms = permissionRepository.findAllByPermissionCodeIn(data.getPermissions());
            roleEntity.setPermissions(new HashSet<>(perms));
        } else {
            roleEntity.setPermissions(new HashSet<>());
        }

        roleRepository.saveAndFlush(roleEntity);

        return ApiResponse.ok(toResponse(roleEntity));
    }

    @Transactional
    @HasPermission(value = {"ROLE_UPDATE"})
    public ApiResponse<RoleResponse> updateRole(ApiRequest<RoleRequest> request){
        RoleRequest data = request.getData();
        if(data.getId() == null){
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        SystemRoleEntity oldEntity = roleRepository.findById(data.getId()).orElseThrow(
                () ->AppException.builder().status(ErrorMessage.NOT_FOUND).build());
        if(!data.getRoleCode().equalsIgnoreCase(oldEntity.getRoleCode())){
            throw AppException.builder().status(ErrorMessage.CANNOT_CHANGE).build();
        }
        oldEntity.setRoleName(data.getRoleName());
        oldEntity.setDescription(data.getDescription());

        if (oldEntity.getPermissions() == null) {
            oldEntity.setPermissions(new HashSet<>());
        } else {
            oldEntity.getPermissions().clear();
        }

        if (data.getPermissions() != null && !data.getPermissions().isEmpty()) {
            List<SystemPermissionEntity> perms = permissionRepository.findAllByPermissionCodeIn(data.getPermissions());
            oldEntity.getPermissions().addAll(perms);
        }

        SystemRoleEntity saved = roleRepository.saveAndFlush(oldEntity);

        return ApiResponse.ok(toResponse(saved));
    }

    private RoleResponse toResponse(SystemRoleEntity entity){
        return RoleResponse.builder()
                .id(entity.getId())
                .roleCode(entity.getRoleCode())
                .roleName(entity.getRoleName())
                .description(entity.getDescription())
                .permissions(entity.getPermissions() != null
                        ? entity.getPermissions().stream().map(SystemPermissionEntity::getPermissionCode).toList()
                        : Collections.emptyList())
                .build();
    }
}
