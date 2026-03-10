package com.community.aspect;

import com.community.annotation.Log;
import com.community.entity.OperationLog;
import com.community.service.OperationLogService;
import com.community.utils.JwtUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;
import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private OperationLogService logService;
    @Autowired
    private JwtUtil jwtUtil;

    @Around("@annotation(com.community.annotation.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        saveLog(point, time);
        return result;
    }

    private void saveLog(ProceedingJoinPoint point, long time) {
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);
            
            OperationLog log = new OperationLog();
            log.setOperation(logAnnotation.value());
            log.setMethod(point.getTarget().getClass().getName() + "." + method.getName());
            log.setParams(JSONUtil.toJsonStr(point.getArgs()));
            log.setTime(time);
            
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            log.setIp(getIpAddress(request));
            
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                log.setUserId(userId);
                log.setUsername(username);
            }
            logService.save(log);
        } catch (Exception e) {
            // 日志记录失败不影响业务
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
