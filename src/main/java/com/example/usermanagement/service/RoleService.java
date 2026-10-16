package com.example.usermanagement.service;

import com.example.usermanagement.config.HasPermission;
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
    public List<RoleResponse> getListRole(){
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @HasPermission(value = {"ROLE_VIEW"})
    public RoleResponse getByRoleCode(String roleCode){
        return toResponse(roleRepository.findByRoleCode(roleCode));
    }

    @Transactional
    @HasPermission(value = {"ROLE_CREATE"})
    public RoleResponse createRole(RoleRequest request){
        if(request.getRoleCode() == null || request.getRoleCode().trim().isEmpty()){
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        if (roleRepository.existsByRoleCode(request.getRoleCode())){
            throw AppException.builder().status(ErrorMessage.ROLE_CODE_EXIST).build();
        }
        SystemRoleEntity roleEntity = new SystemRoleEntity();
        roleEntity.setRoleCode(request.getRoleCode().toUpperCase());
        roleEntity.setRoleName(request.getRoleName());
        roleEntity.setDescription(request.getDescription());

        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            List<SystemPermissionEntity> perms = permissionRepository.findAllByPermissionCodeIn(request.getPermissions());
            roleEntity.setPermissions(new HashSet<>(perms));
        } else {
            roleEntity.setPermissions(new HashSet<>());
        }

        roleRepository.saveAndFlush(roleEntity);

        return toResponse(roleEntity);
    }

    @Transactional
    @HasPermission(value = {"ROLE_UPDATE"})
    public RoleResponse updateRole(RoleRequest request){
        SystemRoleEntity oldEntity = roleRepository.findById(request.getId()).orElseThrow(
                () ->AppException.builder().status(ErrorMessage.NOT_FOUND).build());
        if(!request.getRoleCode().equalsIgnoreCase(oldEntity.getRoleCode())){
            throw AppException.builder().status(ErrorMessage.CANNOT_CHANGE).build();
        }
        oldEntity.setRoleName(request.getRoleName());
        oldEntity.setDescription(request.getDescription());

        // Cập nhật danh sách permissions trên chính instance collection của Hibernate
        if (oldEntity.getPermissions() == null) {
            oldEntity.setPermissions(new HashSet<>());
        } else {
            oldEntity.getPermissions().clear();
        }

        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            List<SystemPermissionEntity> perms = permissionRepository.findAllByPermissionCodeIn(request.getPermissions());
            oldEntity.getPermissions().addAll(perms);
        }

        SystemRoleEntity saved = roleRepository.saveAndFlush(oldEntity);

        return toResponse(saved);
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
