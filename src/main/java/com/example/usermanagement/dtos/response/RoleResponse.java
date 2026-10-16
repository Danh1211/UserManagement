package com.example.usermanagement.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@Builder
public class RoleResponse {

    private Long id;

    private String roleCode;

    private String roleName;

    private String description;

    private List<String> permissions;
}
