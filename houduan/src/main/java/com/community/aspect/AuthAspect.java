package com.community.aspect;

import com.community.annotation.Auth;
import com.community.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuthAspect {

    @Around("@annotation(com.community.annotation.Auth)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String role = (String) request.getAttribute("role");

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Auth authAnnotation = method.getAnnotation(Auth.class);
        String requiredRole = authAnnotation.value();

        if (role == null || !role.equals(requiredRole)) {
            return Result.error(403, "没有权限执行此操作");
        }

        return point.proceed();
    }
}
