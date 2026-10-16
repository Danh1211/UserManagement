package com.example.usermanagement.dtos.response;

import com.example.usermanagement.enums.StatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;

    private String username;

    private String name;

    private String email;

    private StatusEnum status;

    private String roleCode;
}
