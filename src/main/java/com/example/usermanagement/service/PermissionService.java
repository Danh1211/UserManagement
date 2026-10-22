package com.example.usermanagement.service;

import com.example.usermanagement.config.HasPermission;
import com.example.usermanagement.dtos.common.ApiRequest;
import com.example.usermanagement.dtos.common.ApiResponse;
import com.example.usermanagement.dtos.request.PermissionRequest;
import com.example.usermanagement.dtos.response.PermissionResponse;
import com.example.usermanagement.entity.SystemPermissionEntity;
import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.exception.AppException;
import com.example.usermanagement.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    @HasPermission(value = { "PERMISSION_VIEW", "ROLE_CREATE", "ROLE_UPDATE" })
    public ApiResponse<List<PermissionResponse>> getList() {
        return ApiResponse.ok(permissionRepository.findAll().stream().map(this::toResponse).toList());
    }

    @Transactional(readOnly = true)
    @HasPermission(value = { "PERMISSION_VIEW", "ROLE_CREATE", "ROLE_UPDATE" })
    public ApiResponse<PermissionResponse> getByPermissionCode(String permissionCode) {
        SystemPermissionEntity entity = permissionRepository.findByPermissionCode(permissionCode)
                .orElseThrow(() -> AppException.builder().status(ErrorMessage.NOT_FOUND).build());
        return ApiResponse.ok(toResponse(entity));
    }

    @Transactional
    @HasPermission(value = { "PERMISSION_CREATE" })
    public ApiResponse<PermissionResponse> createPermission(ApiRequest<PermissionRequest> request) {
        PermissionRequest data = request.getData();
        if (data.getPermissionCode() == null || data.getPermissionCode().trim().isEmpty()) {
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        if (permissionRepository.existsByPermissionCode(data.getPermissionCode().trim())) {
            throw AppException.builder().status(ErrorMessage.PERMISSION_CODE_EXIST).build();
        }
        SystemPermissionEntity entity = new SystemPermissionEntity();
        entity.setPermissionCode(data.getPermissionCode().trim().toUpperCase());
        entity.setPermissionName(data.getPermissionName());
        entity.setModule(data.getModule());
        entity.setDescription(data.getDescription());
        permissionRepository.saveAndFlush(entity);

        return ApiResponse.ok(toResponse(entity));
    }

    @Transactional
    @HasPermission(value = { "PERMISSION_UPDATE" })
    public ApiResponse<PermissionResponse> updatePermission(ApiRequest<PermissionRequest> request) {
        PermissionRequest data = request.getData();
        if (data.getId() == null) {
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        SystemPermissionEntity oldEntity = permissionRepository.findById(data.getId()).orElseThrow(
                () -> AppException.builder().status(ErrorMessage.NOT_FOUND).build());
        if (!data.getPermissionCode().equalsIgnoreCase(oldEntity.getPermissionCode())) {
            throw AppException.builder().status(ErrorMessage.CANNOT_CHANGE).build();
        }
        oldEntity.setPermissionName(data.getPermissionName());
        oldEntity.setDescription(data.getDescription());
        oldEntity.setModule(data.getModule());
        SystemPermissionEntity saved = permissionRepository.saveAndFlush(oldEntity);

        return ApiResponse.ok(toResponse(saved));
    }

    private PermissionResponse toResponse(SystemPermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return PermissionResponse.builder()
                .id(entity.getId())
                .permissionCode(entity.getPermissionCode())
                .permissionName(entity.getPermissionName())
                .module(entity.getModule())
                .description(entity.getDescription())
                .build();
    }
}
