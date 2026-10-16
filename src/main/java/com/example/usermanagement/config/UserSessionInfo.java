package com.example.usermanagement.config;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionInfo {
    private String fullName;
    private String username;
    private String roleCode;
    private List<String> permissions;
}
