package com.example.usermanagement.dtos.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RoleRequest {

    private Long id;

    private String roleCode;

    private String roleName;

    private String description;

    private List<String> permissions;
}
