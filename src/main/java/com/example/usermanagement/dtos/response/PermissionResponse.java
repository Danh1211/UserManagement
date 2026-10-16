package com.example.usermanagement.dtos.response;

import com.example.usermanagement.enums.ModuleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PermissionResponse {
    private Long id;

    private String permissionCode;

    private String permissionName;

    private ModuleEnum module;

    private String description;
}
