package com.community.aspect;

import com.community.annotation.Auth;
import com.community.common.Result;
import com.community.service.SysPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Aspect
@Component
public class AuthAspect {

    @Autowired
    private SysPermissionService sysPermissionService;

    @Around("@annotation(com.community.annotation.Auth)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String role = (String) request.getAttribute("role");

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Auth authAnnotation = method.getAnnotation(Auth.class);
        String requiredRole = authAnnotation.value();

        if (requiredRole != null && !requiredRole.isBlank()) {
            if (role == null || !role.equals(requiredRole)) {
                return Result.error(403, "没有权限执行此操作");
            }
        }

        String[] requiredPermissions = authAnnotation.permissions();
        if (requiredPermissions != null && requiredPermissions.length > 0) {
            List<String> permissionCodes = sysPermissionService.listPermissionCodesByRole(role);
            if (permissionCodes != null) {
                Set<String> permissionSet = new HashSet<>(permissionCodes);
                boolean hasPermission;
                if (authAnnotation.requireAllPermissions()) {
                    hasPermission = Arrays.stream(requiredPermissions).allMatch(permissionSet::contains);
                } else {
                    hasPermission = Arrays.stream(requiredPermissions).anyMatch(permissionSet::contains);
                }
                if (!hasPermission) {
                    return Result.error(403, "没有权限执行此操作");
                }
            }
        }

        return point.proceed();
    }
}
