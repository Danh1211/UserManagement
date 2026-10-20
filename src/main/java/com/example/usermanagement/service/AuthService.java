package com.example.usermanagement.service;

import com.example.usermanagement.config.UserSessionInfo;
import com.example.usermanagement.dtos.request.LoginRequest;
import com.example.usermanagement.dtos.response.LoginResponse;
import com.example.usermanagement.dtos.response.UserResponse;
import com.example.usermanagement.entity.SystemUserEntity;
import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.enums.StatusEnum;
import com.example.usermanagement.exception.AppException;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.security.CustomUserDetails;
import com.example.usermanagement.security.JwtService;
import com.example.usermanagement.security.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSessionService sessionService;

    public LoginResponse login(LoginRequest request){
        if(!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())){
            throw AppException.builder().status(ErrorMessage.REQUEST_INVALID_PARAMS).build();
        }
        Optional<SystemUserEntity> optional = userRepository.findByUsername(request.getUsername());
        if(optional.isEmpty()){
            throw AppException.builder().status(ErrorMessage.WRONG_USERNAME_PASSWORD).build();
        }
        SystemUserEntity userEntity = optional.orElseGet(optional::get);
        if(!userEntity.getStatus().equals(StatusEnum.ACTIVE)){
            throw AppException.builder().status(ErrorMessage.ACCOUNT_INACTIVE).build();
        }
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())){
            throw AppException.builder().status(ErrorMessage.WRONG_USERNAME_PASSWORD).build();
        }
        if (userEntity.getRoleCode() == null){
            throw AppException.builder().status(ErrorMessage.USER_NOT_HAS_ROLE).build();
        }

        List<GrantedAuthority> authorities =List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRoleCode()));
        UserDetails userDetails = new CustomUserDetails(
                userEntity.getUsername(),
                "",
                authorities,
                userEntity.getName(),
                userEntity.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String token = jwtService.generateToken(authentication);

        List<String> permissions = roleRepository.findAllPermissionByRoleCode(userEntity.getRoleCode());
        UserSessionInfo userSessionInfo = UserSessionInfo.builder()
                .username(userEntity.getUsername())
                .fullName(userEntity.getName())
                .email(userEntity.getEmail())
                .roleCode(userEntity.getRoleCode())
                .permissions(permissions)
                .build();

        sessionService.cacheSession(userSessionInfo.getUsername(), userSessionInfo);
        sessionService.cacheToken(userSessionInfo.getUsername(), token);
        return LoginResponse.builder()
                .fullName(userEntity.getName())
                .username(userEntity.getUsername())
                .accessToken(token)
                .permissions(permissions)
                .build();
    }

    public UserResponse getUser(String username){
        SystemUserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> AppException.builder().status(ErrorMessage.NOT_FOUND).build());
        return toResponse(entity);
    }

    private UserResponse toResponse(SystemUserEntity entity){
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .roleCode(entity.getRoleCode())
                .build();
    }
}
