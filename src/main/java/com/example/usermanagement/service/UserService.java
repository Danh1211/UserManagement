package com.example.usermanagement.service;

import com.example.usermanagement.config.HasPermission;
import com.example.usermanagement.dtos.request.ToggleRoleRequest;
import com.example.usermanagement.dtos.response.UserResponse;
import com.example.usermanagement.entity.SystemUserEntity;
import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.exception.AppException;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @HasPermission(value = {"USER_VIEW"})
    public List<UserResponse> list(){
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    @HasPermission(value = {"USER_UPDATE"})
    public UserResponse toggleRole(ToggleRoleRequest request){
        if(request.getUsername().isEmpty() || request.getRoleCode().isEmpty()){
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        Optional<SystemUserEntity> optional = userRepository.findByUsername(request.getUsername());
        if(optional.isEmpty()){
            throw AppException.builder().status(ErrorMessage.NOT_FOUND).build();
        }
        SystemUserEntity entity = optional.orElseGet(optional::get);
        entity.setRoleCode(request.getRoleCode());
        userRepository.save(entity);

        return toResponse(entity);
    }

    private UserResponse toResponse(SystemUserEntity entity){
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .name(entity.getName())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .roleCode(entity.getRoleCode())
                .build();
    }
}
