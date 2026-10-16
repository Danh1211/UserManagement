package com.example.usermanagement.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String username;

    private String fullName;

    private String accessToken;

    private List<String> permissions;
}
