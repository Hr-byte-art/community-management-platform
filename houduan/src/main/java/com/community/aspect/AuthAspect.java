package com.community.aspect;

import com.community.annotation.Auth;
import com.community.common.Result;
import com.community.service.SysPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthAspect.class);
    private static final String AI_TRACE_ID_HEADER = "X-AI-Trace-Id";

    @Autowired
    private SysPermissionService sysPermissionService;

    @Around("@annotation(com.community.annotation.Auth)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long authStartTime = System.currentTimeMillis();
        String role = (String) request.getAttribute("role");

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Auth authAnnotation = method.getAnnotation(Auth.class);
        String requiredRole = authAnnotation.value();
        String[] requiredPermissions = authAnnotation.permissions();

        if (requiredRole != null && !requiredRole.isBlank()) {
            if (role == null || !role.equals(requiredRole)) {
                logAiAuth(request, role, requiredPermissions, authStartTime, false);
                return Result.error(403, "\u6ca1\u6709\u6743\u9650\u6267\u884c\u6b64\u64cd\u4f5c");
            }
        }

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
                    logAiAuth(request, role, requiredPermissions, authStartTime, false);
                    return Result.error(403, "\u6ca1\u6709\u6743\u9650\u6267\u884c\u6b64\u64cd\u4f5c");
                }
            }
        }

        logAiAuth(request, role, requiredPermissions, authStartTime, true);
        return point.proceed();
    }

    private void logAiAuth(HttpServletRequest request, String role, String[] requiredPermissions,
                           long authStartTime, boolean granted) {
        if (request == null || request.getRequestURI() == null || !request.getRequestURI().contains("/ai/complete")) {
            return;
        }
        String traceId = request.getHeader(AI_TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = "unknown";
        }
        logger.info("[ai-complete][backend] traceId={} stage=auth uri={} granted={} role={} requiredPermissions={} costMs={}",
                traceId,
                request.getRequestURI(),
                granted,
                role,
                Arrays.toString(requiredPermissions),
                System.currentTimeMillis() - authStartTime);
    }
}
