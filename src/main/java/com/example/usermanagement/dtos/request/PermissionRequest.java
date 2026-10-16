package com.example.usermanagement.dtos.request;

import com.example.usermanagement.enums.ModuleEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequest {

    private Long id;

    private String permissionCode;

    private String permissionName;

    private ModuleEnum module;

    private String description;
}
