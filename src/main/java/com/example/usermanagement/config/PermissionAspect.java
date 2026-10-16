package com.example.usermanagement.config;

import com.example.usermanagement.enums.ErrorMessage;
import com.example.usermanagement.exception.AppException;
import com.example.usermanagement.exception.NoAuthenticationError;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(hasPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, HasPermission hasPermission) throws Throwable{
        String[] requiredPermission = hasPermission.value();
        PermissionAspect.hasPermission(Arrays.asList(requiredPermission));
        return joinPoint.proceed();
    }

    public static void hasPermission(List<String> requiredPermission){
        UserSessionInfo sessionInfo = UserContext.get();
        if (ObjectUtils.isEmpty(sessionInfo) || ObjectUtils.isEmpty(sessionInfo.getPermissions())){
            throw new NoAuthenticationError(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện chức năng này");
        }
        List<String> permission = sessionInfo.getPermissions();
        boolean allowed = requiredPermission.stream().anyMatch(permission::contains);
        if(!allowed){
            throw new NoAuthenticationError(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện chức năng này");
        }
    }
}
