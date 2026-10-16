package com.example.usermanagement.service;

import com.example.usermanagement.config.HasPermission;
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
    public List<PermissionResponse> getList() {
        return permissionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    @HasPermission(value = { "PERMISSION_VIEW", "ROLE_CREATE", "ROLE_UPDATE" })
    public PermissionResponse getByPermissionCode(String permissionCode) {
        return toResponse(permissionRepository.findByPermissionCode(permissionCode));
    }

    @Transactional
    @HasPermission(value = { "PERMISSION_CREATE" })
    public PermissionResponse createPermission(PermissionRequest request) {
        if (request.getPermissionCode() == null || request.getPermissionCode().trim().isEmpty()) {
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        if (permissionRepository.existsByPermissionCode(request.getPermissionCode().trim())) {
            throw AppException.builder().status(ErrorMessage.PERMISSION_CODE_EXIST).build();
        }
        SystemPermissionEntity entity = new SystemPermissionEntity();
        entity.setPermissionCode(request.getPermissionCode().trim().toUpperCase());
        entity.setPermissionName(request.getPermissionName());
        entity.setModule(request.getModule());
        entity.setDescription(request.getDescription());
        permissionRepository.saveAndFlush(entity);

        return toResponse(entity);
    }

    @Transactional
    @HasPermission(value = { "PERMISSION_UPDATE" })
    public PermissionResponse updatePermission(PermissionRequest request) {
        if (request.getId() == null) {
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        SystemPermissionEntity oldEntity = permissionRepository.findById(request.getId()).orElseThrow(
                () -> AppException.builder().status(ErrorMessage.NOT_FOUND).build());
        if (!request.getPermissionCode().equalsIgnoreCase(oldEntity.getPermissionCode())) {
            throw AppException.builder().status(ErrorMessage.CANNOT_CHANGE).build();
        }
        oldEntity.setPermissionName(request.getPermissionName());
        oldEntity.setDescription(request.getDescription());
        oldEntity.setModule(request.getModule());
        SystemPermissionEntity saved = permissionRepository.saveAndFlush(oldEntity);

        return toResponse(saved);
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
