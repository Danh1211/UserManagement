package com.example.usermanagement.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleRoleRequest {

    private String username;

    private String roleCode;
}
